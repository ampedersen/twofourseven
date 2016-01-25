package com.molamil.radio24syv.api;

import retrofit.Callback;

/**
 * Created by patriksvensson on 25/01/16.
 * http://stackoverflow.com/a/23271559
 */

public interface CancelableCallback<T> extends Callback<T>
{
    public void cancel();
    public boolean isCanceled();
}

