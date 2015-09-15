#include "myFilters.h"
#include "SuperpoweredExample.h"
//include "jni.h"
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
//#include "log.h"


//initialize SuperpoweredPlayer
static void playerEventCallback(void *clientData,
	SuperpoweredAdvancedAudioPlayerEvent event, void *value){
	if (event ==
		SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
		  SuperpoweredAdvancedAudioPlayer *player = *
			((SuperpoweredAdvancedAudioPlayer **)clientData);
		  player->setPosition(player->firstBeatMs, false, false);

	}
}



static void openSLESCallback(SLAndroidSimpleBufferQueueItf caller, void *pContext) {
	((myFilters *)pContext)->process(caller);
}



myFilters::myFilters(const char *path) : currentBuffer(0), min(0.0f), max(0.0f), FREQS{ 25, 100, 300, 700, 1500, 3100, 6300, 12700 }  {
	

	
	

	for (int n = 0; n < NUM_BUFFERS; n++) outputBuffer[n] =
		(float *)memalign(16, (buffersize + 16) * sizeof(float) * 2);
	unsigned int samplerate = 44100;

		
	filt1 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt2 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt3 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt4 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt5 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt6 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt7 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	filt8 = new SuperpoweredFilter(SuperpoweredFilter_Parametric, samplerate);
	
}

myFilters::~myFilters(){
	for (int n = 0; n < NUM_BUFFERS; n++) free(outputBuffer[n]);
}


#define MAX1 150.0f 
#define MIN1 70.0f
#define MAX2 300.0f
#define MIN2 MAX2+.04f
#define MAX3 700.0f
#define MIN3 MAX2+.04f
#define MAX4 1600.0f
#define MIN4 MAX3+.04f
#define MAX5 3500.0f
#define MIN5 MAX4+.04f
#define MAX6 7800.0f
#define MIN6 MAX5+.04f
#define MAX7 15000.0f
#define MIN7 MAX6+.04f
#define MAX8 20000.0f
#define MIN8 MAX7+.04f
static inline float floatToFrequency(float value, int* freq, int index) {
	//slider will work logarithmically with frequencies now.
	int MAXFREQ = freq[index + 1], MINFREQ = freq[index];
	if (value > 0.97f) return MAXFREQ;
	if (value < 0.03f) return MINFREQ;
	value = powf(10.0f, (value + ((0.4f - fabsf(value - 0.4f)) * 0.3f)) * log10f(MAXFREQ - MINFREQ)) + MINFREQ;
	return value < MAXFREQ ? value : MAXFREQ;
}



void myFilters::setFilt(int vol/*percent*/, int filtNum, float q /*q should be between .01 and 4*/){
	float value = float(vol) * 0.01f;
	
	switch (filtNum) {
	case 1:
		filt1->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt1->enable(true);
		break;
	case 2:
		filt2->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt2->enable(true);
		break;
	case 3:
		filt3->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt3->enable(true);
		break;
	case 4:
		filt4->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt4->enable(true);
		break;
	case 5:
		filt5->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt5->enable(true);
		break;
	case 6:
		filt6->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt6->enable(true);
		break;
	case 7:
		filt7->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt7->enable(true);
		break;
	case 8:
		filt8->setParametricParameters(floatToFrequency(value, FREQS, filtNum), q, value);
		filt8->enable(true);
		break;
	default:
		filt1->setParametricParameters(FREQS[0], .7, 1.0f);
		filt2->setParametricParameters(FREQS[1], .7, 1.0f);
		filt3->setParametricParameters(FREQS[2], .7, 1.0f);
		filt4->setParametricParameters(FREQS[3], .7, 1.0f);
		filt5->setParametricParameters(FREQS[4], .7, 1.0f);
		filt6->setParametricParameters(FREQS[5], .7, 1.0f);
		filt7->setParametricParameters(FREQS[6], .7, 1.0f);
		filt8->setParametricParameters(FREQS[7], .7, 1.0f);
	}
}

jfloatArray myFilters::process(jshortArray input, jint input_length) {
	
		JNIEnv *env;
		jshort     *input_array_elements;
		//int         input_length;

		jfloat      *output_array_elements;
		jfloatArray  output;

		input_array_elements = (*env)->GetShortArrayElements(env, input, 0);


		output = (jbyteArray)((*env)->NewByteArray(env, input_length * 2));
		output_array_elements = (*env)->GetByteArrayElements(env, output, 0);

		memcpy(output_array_elements, input_array_elements, input_length * 2);



		
	



	
	jfloat* converter = env->GetFloatArrayElements( output, 0);
	float* current = converter;
	pthread_mutex_lock(&mutex);
	
	float *stereoBuffer = outputBuffer[currentBuffer]; //this converts from androidbufferqueue to float*, what we need, but is only for offline files.
	//This is serial processing
	filt1->process(current, current, buffersize);
	filt2->process(current, current, buffersize);
	filt3->process(current, current, buffersize);
	filt4->process(current, current, buffersize);
	filt5->process(current, current, buffersize);
	filt6->process(current, current, buffersize);
	filt7->process(current, current, buffersize);
	filt8->process(current, current, buffersize);
	pthread_mutex_unlock(&mutex);
    jfloat* converter = current;
	(*env)->ReleaseShortArrayElements(env, input, input_array_elements, JNI_ABORT);
    (*env)->ReleaseFloatArrayElements(env, output, output_array_elements, 0);
    return current;
	//This is a float buffer =D


	
	//All filters are now processed on the input and added together.
	
}


extern "C" {
	JNIEXPORT void Java_peaston_android_cogswell_edu_lowlvleq_MainActivity_myFilters(JNIEnv *javaEnvironment, jobject self, jstring apkPath);

	JNIEXPORT jfloatArray Java_peaston_android_cogswell_edu_lowlvleq_MyAudioController_process(JNIEnv *javaEnvironment, jobject self, jfloatArray input, jint input_length);

	JNIEXPORT void Java_peaston_android_cogswell_edu_lowlvleq_MainActivity_setFilt(JNIEnv *javaEnvironment, jobject self, jint vol, jint filt, jfloat q);
	
	

}

static myFilters *example = NULL;

JNIEXPORT void Java_peaston_android_cogswell_edu_lowlvleq_MainActivity_myFilters(JNIEnv *javaEnvironment, jobject self, jstring apkPath){
	

	const char *path = javaEnvironment->GetStringUTFChars(apkPath, JNI_FALSE);
	example = new myFilters(path);
	javaEnvironment->ReleaseStringUTFChars(apkPath, path);

}

JNIEXPORT jfloatArray Java_peaston_android_cogswell_edu_lowlvleq_MyAudioController_process
(JNIEnv *javaEnvironment, jobject self, jfloatArray input, jint input_length){
	example->process(input);
}

JNIEXPORT void Java_peaston_android_cogswell_edu_lowlvleq_MainActivity_setFilt(JNIEnv *javaEnvironment, jobject self, jint vol, jint filtNum, jfloat q){
	example->setFilt(vol, filtNum, q);

	
}

