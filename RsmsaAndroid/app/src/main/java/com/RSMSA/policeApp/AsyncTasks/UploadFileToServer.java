package com.RSMSA.policeApp.AsyncTasks;

/**
 * Created by Ilakoze on 2/26/2015.
 */

import android.os.AsyncTask;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;

import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.Utils.AndroidMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;


