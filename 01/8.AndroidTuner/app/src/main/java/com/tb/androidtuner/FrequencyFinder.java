package com.tb.androidtuner;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FrequencyFinder {

    private double[] window;

    public FrequencyFinder() {
        window = null;
    }

    public double getFrequency(short[] sampleData, int sampleRate) {

        double t = 0;
        /* sampleData + zero padding */
        DoubleFFT_1D fft = new DoubleFFT_1D(sampleData.length + 24 * sampleData.length);
        double[] a = new double[(sampleData.length + 24 * sampleData.length) * 2 *4];
        //double[] b = new double[(a.length + 24 * a.length) * 2];
        System.arraycopy(applyHammingWindow(sampleData), 0, a, 0, sampleData.length);
        fft.realForward(a);
        //double[] a = fftAutoCorrelation(sampleData);

        /* find the peak magnitude and it's index */
        double maxMag = Double.NEGATIVE_INFINITY;
        int maxInd = -1;

        for(int i = 0; i < a.length / 2; ++i) {
            double re  = a[2*i];
            double im  = a[2*i+1];
            double mag = Math.sqrt(re * re + im * im);

            t = (double)sampleRate * i /(a.length/2);

            if(mag > maxMag && t <55) {
                maxMag = mag;
                maxInd = i;
            }
        }
        /* calculate the frequency */
        return (double)sampleRate * maxInd /(a.length/2);
    }

    public double calculatePowerSpectrum(List<Double> data) {
        List<Double> result = new ArrayList<Double>();

        int fftSize = data.size();

        DoubleFFT_1D fftTransform = new DoubleFFT_1D(fftSize);
        double[] fftData = prepareDataArray(data, fftSize, true);

        fftTransform.complexForward(fftData);

        double max = 0.0f;
        double maxindex = 0;

        for (int i = 0; i < fftSize/2; i++) {
            // calculate powerspectrum as sqrt(re*re+im*im)
            double fr = Math.sqrt(fftData[2 * i] * fftData[2 * i]
                    + fftData[2 * i + 1] * fftData[2 * i + 1]);
            result.add(fr);

            if (max < fr)
            {
                max = fr;
                maxindex = i;
            }
        }

        return 44100.0*maxindex/fftSize;
    }


    public double[] prepareDataArray(List<Double> data, int fftSize,
                                     boolean complex) {
        int mul = 1; // multiplier for complex numbers
        int add = 0; // shift for complex numbers
        if (complex) {
            mul = 2;
            add = 1;
        }

        double[] result = new double[mul * fftSize];

        // array contains [real1,complex1,real2,complex2,...,realn,complexn]
        // array gets padded with zero if datasize<fftsize
        // if complex = false
        for (int i = 0; i < fftSize; i++) {
            if (i < data.size()) {
                result[mul * i + add] = 0;
                result[mul * i] = data.get(i);

            } else {
                result[mul * i + add] = 0;
                result[mul * i] = 0;
            }
        }
        return result;
    }



    private void buildHammingWindow(int size) {
        if(window != null && window.length == size) {
            return;
        }
        window = new double[size];
        for(int i = 0; i < size; ++i) {
            window[i] = .54 - .46 * Math.cos(2 * Math.PI * i / (size - 1.0));
        }
    }

    private double[] applyHammingWindow(short[] input) {
        double[] res = new double[input.length];

        buildHammingWindow(input.length);
        for(int i = 0; i < input.length-1; ++i) {
            res[i] = (double)input[i] * window[i];
        }
        return res;
    }
    //in case we need bigger input-works much slower.
    private double[] applyHammingWindow(double[] input) {
        double[] res = new double[input.length];

        buildHammingWindow(input.length);
        for(int i = 0; i < input.length-1; ++i) {
            res[i] = input[i] * window[i];
        }
        return res;
    }

    //not used - works slower + not working properly.
    public double[]  fftAutoCorrelation(short[] sampleData) {

        //DoubleFFT_1D fftt = new DoubleFFT_1D(sampleData.length + 24 * sampleData.length);

        double[] a = new double[sampleData.length];
        double [] ac = new double [a.length];


        int n = a.length;
        // Assumes n is even.

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(a);
        ac[0] = Math.sqrt(a[0]);

        ac[0] = 0;
        ac[1] = Math.sqrt(a[1]);
        for (int i = 2; i < n; i += 2) {
            ac[i] = Math.sqrt(a[i]) + Math.sqrt(a[i + 1]);
            ac[i+1] = 0;
        }
        DoubleFFT_1D ifft = new DoubleFFT_1D(n);
        ifft.realInverse(ac, true);

        // For statistical convention, normalize by dividing through with variance
        //for (int i = 1; i < n; i++)
        //    ac[i] /= ac[0];
        //ac[0] = 1;

    return ac;
    }

}



