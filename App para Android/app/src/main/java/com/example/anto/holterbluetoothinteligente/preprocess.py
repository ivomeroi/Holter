import pandas as pd
import numpy as np
import sys
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
from scipy.fft import fft, ifft
from scipy import signal

SEGUNDO_ECG = 148

def get_ecg(ecg_array):
    df = ecg_array

    return df

def butter_pass(cutoff, fs, btype, order=5):
    nyq = 0.5 * fs
    normal_cutoff = cutoff / nyq
    b, a = signal.butter(order, normal_cutoff, btype = btype, analog = False)
    return b, a

def butter_pass_filter(data, cutoff, fs, type, order=5):
    b, a = butter_pass(cutoff, fs, order=order, btype=type)
    y = signal.filtfilt(b, a, data)
    return y

def noise_filter(df, low=False, high=False):
    filtered_df = [item for sublist in df.values for item in sublist]
    if low:
        fps_low = 20
        filtered_df = butter_pass_filter(filtered_df, 3, fps_low, 'low')
    if high:
        fps_high = SEGUNDO_ECG
        filtered_df = butter_pass_filter(filtered_df, 10, fps_high, 'high')

    return filtered_df

def main():
    ecg_array = []
    ecg_as_string = sys.argv[1]
    ecg_as_string = ecg_as_string.replace('[','').replace(']','').strip().split(',')
    for i in range(1, len(ecg_as_string)):
        ecg_array.append(int(ecg_as_string[i]))

    df = get_ecg(ecg_array)

    signal_filtered = noise_filter(df, low=True)

    print(signal_filtered)

main()