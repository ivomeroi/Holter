beat=0;
list={'mitdb/100','mitdb/101','mitdb/102','mitdb/103','mitdb/104','mitdb/105','mitdb/106','mitdb/107','mitdb/108','mitdb/109','mitdb/111','mitdb/112','mitdb/113','mitdb/114','mitdb/115','mitdb/116','mitdb/117','mitdb/118','mitdb/119','mitdb/121','mitdb/122','mitdb/123','mitdb/124','mitdb/200','mitdb/201','mitdb/202','mitdb/203','mitdb/205','mitdb/207','mitdb/208','mitdb/209','mitdb/210','mitdb/212','mitdb/213','mitdb/214','mitdb/215','mitdb/217','mitdb/219','mitdb/220','mitdb/221','mitdb/222','mitdb/223','mitdb/228','mitdb/230','mitdb/231','mitdb/232','mitdb/233','mitdb/234'};

for k=1:48
    [t,y]=rdsamp(list{k},1);
    mf1=medfilt1(y,71);
    mf2=medfilt1(mf1,215);
    y=y-mf2;
    y=smooth(y);
    [ann,type,sub]=rdann(list{k},'atr');
    
    vf=0;
    noise=0;
    
    ann2loc=find(type~='+' & type~='"' & type~='x' & type~='|');
    ann2=ann(ann2loc);
    type2=type(ann2loc);
    sub2=sub(ann2loc);
    ann3loc=find(type2~='[' & type2~=']' & type2~='~');
    ann3=ann2(ann3loc);
    type3=type2(ann3loc);
    
    for i=13:length(ann2)-3
        if type2(i)=='['
            vf=1;
        else if type2(i)==']'
                vf=0;
            else if type2(i)=='~'
                    if (sub2(i)=='1' || sub2(i)=='3') && noise==0
                        noise=1;
                    end
                    
                    if (sub2(i)=='0' || sub2(i)=='2') && noise==1
                        noise=0;
                    end
                else if vf==0 & noise==0 & ann2(i)>90 & ann2(i)<(ann2(end)-90) & (type2(i)=='N' | type2(i)=='V' | type2(i)=='S' | type2(i)=='A' | type2(i)=='J' | type2(i)=='L' | type2(i)=='R' | type2(i)=='/')
                        beat=beat+1;
                        morphfeat(:,beat)=y(ann2(i)-90:ann2(i)+90);
                        
                        j=find(ann3==ann2(i));
                        prerr(:,beat)=ann3(j)-ann3(j-1);
                        postrr(:,beat)=ann3(j+1)-ann3(j);
                        avgrr(:,beat)=(ann3(j)-ann3(j-10))/10;
                        tempfeat(:,beat)=[prerr(:,beat); postrr(:,beat); avgrr(:,beat)];
                        
                        if type2(i)=='N' | type2(i)=='L' | type2(i)=='R'
                            target(:,beat)=[1 0 0 0];
                        else if type2(i)=='V'
                                target(:,beat)=[0 1 0 0];
                            else if type2(i)=='A' | type2(i)=='S' | type2(i)=='J'
                                    target(:,beat)=[0 0 1 0];
                                else if type2(i)=='/'
                                        target(:,beat)=[0 0 0 1];
                                    end
                                end
                            end
                        end
                    end
                end
            end
        end
    end
end

bestnetperf=1;
bestrunperf=1;
performance=[];
nets=cell(50);
trs=cell(50);

ntotal=find(target(1,:));
vtotal=find(target(2,:));
stotal=find(target(3,:));
ptotal=find(target(4,:));

nrdm=ntotal(randperm(length(ntotal)));
vrdm=vtotal(randperm(length(vtotal)));
srdm=stotal(randperm(length(stotal)));
prdm=ptotal(randperm(length(ptotal)))

trainindex=[nrdm(1:4156) vrdm(1:4156) srdm(1:1519) prdm(1:4129)];
valindex=[nrdm(4157:5542) vrdm(4157:5542) srdm(1520:2026) prdm(4130:5505)];
testindex=[nrdm(5543:length(vtotal)) vrdm(5543:length(vtotal)) srdm(2027:length(stotal)) prdm(5506:length(ptotal))];
trainmorphfeat=morphfeat(:,trainindex);
valmorphfeat=morphfeat(:,valindex);
testmorphfeat=morphfeat(:,testindex);
traintempfeat=tempfeat(:,trainindex);
valtempfeat=tempfeat(:,valindex);
testtempfeat=tempfeat(:,testindex);
traintarget=target(:,trainindex);
valtarget=target(:,valindex);
testtarget=target(:,testindex);

[trainstd,ps1]=mapstd(trainmorphfeat);
[trainpca,ps2]=processpca(trainstd,0.01);
valstd=mapstd('apply',valmorphfeat,ps1);
valpca=processpca('apply',valstd,ps2);
teststd=mapstd('apply',testmorphfeat,ps1);
testpca=processpca('apply',teststd,ps2);

[traintempfeat,ps3]=mapstd(traintempfeat);
valtempfeat=mapstd('apply',valtempfeat,ps3);
testtempfeat=mapstd('apply',testtempfeat,ps3);

netinput=vertcat([trainpca valpca testpca]);
nettarget=[traintarget valtarget testtarget];

trainsize=length(trainindex);
valsize=length(valindex);
testsize=length(testindex);

testinput=netinput(:,6401:8000);
testtarget=nettarget(:,6401:8000);

learningrate=[0.005 0.01 0.1 0.5];

for j=1:4
    for i=1:50
        net=patternnet(i);
        net.divideFcn='divideind';
        net.divideParam.trainInd=1:trainsize;
        net.divideParam.valInd=trainsize+1:trainsize+valsize;
        net.divideParam.testInd=trainsize+valsize+1:trainsize+valsize+testsize;
        net.trainParam.max_fail = 20;
        net.input.processFcns = {};
        net.output.processFcns = {};
        net.inputWeights{1,1}.learnParam.lr = learningrate(j);
        net.layerWeights{2,1}.learnParam.lr = learningrate(j);
        
        [net,tr]=train(net,netinput,nettarget);
        nets{i,j}=net;
        trs{i,j}=tr;
        performance(i,j)=tr.best_vperf;
    end
end

[minperf1,hn]=min(performance);
[minperf2,lr]=min(min(performance));
bestnet=nets{hn(lr),lr};
bestnettr=trs{hn(lr),lr};
view(bestnet);
plotconfusion(testtarget,bestnet(testinput));
plotperf(bestnettr);
plotroc(testtarget,bestnet(testinput));