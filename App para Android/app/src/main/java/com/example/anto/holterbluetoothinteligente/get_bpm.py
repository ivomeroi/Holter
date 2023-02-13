import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import sys
import matplotlib.pyplot as plt
from scipy.fft import fft, ifft
from scipy import signal

SEGUNDO_ECG = 148

def get_ecg(ecg_array):
    df = ecg_array

    return df

def get_bpm(df):
    flat_values = [item for sublist in df.values for item in sublist]
    ecg_data = pd.Series(flat_values)

    # Detección de picos R en la señal de ECG.
    peaks, _ = signal.find_peaks(ecg_data, distance=(SEGUNDO_ECG * 0.6), height=2)
    distancias = np.diff(peaks)

    media = np.mean(distancias)
    #print(type(media))

    # Calcular y mostrar los latidos por minuto (BPM).
    bpm = (ecg_data.size/media)/(ecg_data.size/(SEGUNDO_ECG * 60))

    return round(bpm)


def main():
    ecg_array = []
    ecg_as_string = sys.argv[1]
    ecg_as_string = ecg_as_string.replace('[','').replace(']','').strip().split(',')
    for i in range(1, len(ecg_as_string)):
        ecg_array.append(int(ecg_as_string[i]))

    df = get_ecg(ecg_array)

    bpm = get_bpm(df)

    print(bpm)

main()
