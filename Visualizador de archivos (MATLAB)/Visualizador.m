function varargout = Visualizador(varargin)
% VISUALIZADOR MATLAB code for Visualizador.fig
%      VISUALIZADOR, by itself, creates a new VISUALIZADOR or raises the existing
%      singleton*.
%
%      H = VISUALIZADOR returns the handle to a new VISUALIZADOR or the handle to
%      the existing singleton*.
%
%      VISUALIZADOR('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in VISUALIZADOR.M with the given input arguments.
%
%      VISUALIZADOR('Property','Value',...) creates a new VISUALIZADOR or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before Visualizador_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to Visualizador_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help Visualizador

% Last Modified by GUIDE v2.5 11-Nov-2016 12:33:42

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
    'gui_Singleton',  gui_Singleton, ...
    'gui_OpeningFcn', @Visualizador_OpeningFcn, ...
    'gui_OutputFcn',  @Visualizador_OutputFcn, ...
    'gui_LayoutFcn',  [] , ...
    'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT

% --- Executes just before Visualizador is made visible.
function Visualizador_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to Visualizador (see VARARGIN)

% Choose default command line output for Visualizador
handles.output = hObject;
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
guidata(hObject, handles);

[filename,path]=uigetfile('*.json','Seleccione el archivo a visualizar');

if (filename~=0)
    data=loadjson(filename);
    t=0:(1/360):(1/360)*(length(data.senal)-1);
    plot(t,data.senal);
    hold on;
    plot(t(data.qrs+1),data.senal(data.qrs+1),'or');
    latidosnoclas=length(data.qrs)-length(data.clases);
    
    for i=1:length(data.clases)
        text(t(data.qrs(i+latidosnoclas-1)),data.senal(data.qrs(i+latidosnoclas-1))+0.2,data.clases(i),'HorizontalAlignment','center', 'Clipping','on');
        text(t(data.qrs(i+latidosnoclas-1)),data.senal(data.qrs(i+latidosnoclas-1))+0.3,data.actividades(i),'HorizontalAlignment','center','Clipping','on');
    end
    
    for i=1:length(data.qrssintomas)
        text(t(data.qrssintomas(i)),data.senal(data.qrssintomas(i))+0.4,data.sintomas(i),'HorizontalAlignment','center', 'Clipping','on');
    end
    
    axes=handles.axes1;
    axes.XLimMode='manual';
    axes.XLim=[0 2];
    axes.XGrid='on';
    axes.YGrid='on';
    axes.YLim=[-1 2];
    axes.XMinorGrid='on';
    axes.YMinorGrid='on';
    axes.XTickMode='manual';
    axes.XTick=0:0.2:t(end);
    axes.XAxis.MinorTickValuesMode='manual';
    axes.XAxis.MinorTickValues=0:0.04:t(end);
    axes.YTickMode='manual';
    axes.YAxis.MinorTickValuesMode='manual';
    axes.YTick=-1:0.5:2;
    axes.YAxis.MinorTickValues=-1:0.1:2;
    pan xon;
    
    if (t(end)>3600)
        hora=data.hora-1;
    else
        minutos=round(data.minutos-t(end)/60);
        
        if minutos<0
            minutos=abs(minutos);
            hora=data.hora-1;
        else
            hora=data.hora;
        end
    end
    
    handles.text6.String = [num2str(hora) ':' num2str(minutos,'%2.2d')];
    handles.text7.String = [num2str(data.hora) ':' num2str(data.minutos,'%2.2d')];
    handles.text9.String = data.evento;
end


% Update handles structure

% UIWAIT makes Visualizador wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = Visualizador_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes during object creation, after setting all properties.
function axes1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to axes1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: place code in OpeningFcn to populate axes1


% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(hObject, eventdata, handles)
hold off;
[filename,path]=uigetfile('*.json','Seleccione el archivo a visualizar');

if (filename~=0)
    data=loadjson(filename);
    t=0:(1/360):(1/360)*(length(data.senal)-1);
    plot(t,data.senal);
    hold on;
    plot(t(data.qrs+1),data.senal(data.qrs+1),'or');
    latidosnoclas=length(data.qrs)-length(data.clases);
        
    for i=1:length(data.clases)
        text(t(data.qrs(i+latidosnoclas-1)),data.senal(data.qrs(i+latidosnoclas-1))+0.2,data.clases(i),'HorizontalAlignment','center', 'Clipping','on');
        text(t(data.qrs(i+latidosnoclas-1)),data.senal(data.qrs(i+latidosnoclas-1))+0.3,data.actividades(i),'HorizontalAlignment','center','Clipping','on');
    end
    
    for i=1:length(data.qrssintomas)
        text(t(data.qrssintomas(i)),data.senal(data.qrssintomas(i))+0.4,data.sintomas(i),'HorizontalAlignment','center', 'Clipping','on');
    end
    
    axes=handles.axes1;
    axes.XLimMode='manual';
    axes.XLim=[0 2];
    axes.XGrid='on';
    axes.YGrid='on';
    axes.YLim=[-1 2];
    axes.XMinorGrid='on';
    axes.YMinorGrid='on';
    axes.XTickMode='manual';
    axes.XTick=0:0.2:t(end);
    axes.XAxis.MinorTickValuesMode='manual';
    axes.XAxis.MinorTickValues=0:0.04:t(end);
    axes.YTickMode='manual';
    axes.YAxis.MinorTickValuesMode='manual';
    axes.YTick=-1:0.5:2;
    axes.YAxis.MinorTickValues=-1:0.1:2;
    pan xon;
    
    if (t(end)>3600)
        hora=data.hora-1;
    else
        minutos=round(data.minutos-t(end)/60);
        
        if minutos<0
            minutos=abs(minutos);
            hora=data.hora-1;
        else
            hora=data.hora;
        end
    end
    
    handles.text6.String = [num2str(hora) ':' num2str(minutos,'%2.2d')];
    handles.text7.String = [num2str(data.hora) ':' num2str(data.minutos,'%2.2d')];
    handles.text9.String = data.evento;
end