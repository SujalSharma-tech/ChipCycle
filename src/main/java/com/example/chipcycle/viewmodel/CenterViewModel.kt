package com.example.chipcycle.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chipcycle.models.LocationByStateData
import com.example.chipcycle.models.LocationData
import com.example.chipcycle.models.LocationResponseCenters
import com.example.chipcycle.models.ResponseData
import com.example.chipcycle.retrofitClient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CenterViewModel : ViewModel() {
    // Location data
    private val _locationData = MutableLiveData<LocationData>()
    val locationData: LiveData<LocationData> = _locationData as LiveData<LocationData>

    private val _centerList = MutableLiveData<List<LocationResponseCenters>?>()
    val centerList: LiveData<List<LocationResponseCenters>> = _centerList as LiveData<List<LocationResponseCenters>>


    // Response data from API
    private val _responseData = MutableLiveData<ResponseData>()
    val responseData: LiveData<ResponseData> = _responseData

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _centersLength = MutableLiveData<Int>()
    val centersLength: LiveData<Int> = _centersLength

    // Error message
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Status message for progress updates
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    // Set current location data
    fun setLocationData(data: LocationData) {
        _locationData.value = data
        sendLocationToApi(data)
    }

    // API call to send location data
     fun sendLocationToApi(locationData: LocationData) {
        _isLoading.value = true
//        _statusMessage.value = "Sending location..."

        RetrofitInstance.apiInterface.sendLocation(locationData).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                _isLoading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    _responseData.value = responseData
                    _centerList.value = responseData.centersList
                    Log.i("LiveData",_centerList.toString())
                    Log.i("Centers1", responseData.centersList.toString())

                    if (responseData.success) {
//                        _statusMessage.value = "Location sent successfully"
                    } else {
                        _errorMessage.value = "Error: ${responseData.success}"
                    }
                } else {
                    _errorMessage.value = "Failed to send location: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.localizedMessage}"
            }
        })


    }

    fun sendState(locationData: LocationByStateData) {
        _isLoading.value = true
        _statusMessage.value = "Sending location..."

        RetrofitInstance.apiInterface.sendState(locationData).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                _isLoading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    _responseData.value = responseData
                    _centerList.value = responseData.centersList
                    _centersLength.value = responseData.centersList.size
                    Log.i("LiveData",_centerList.toString())
                    Log.i("Centers1", responseData.centersList.toString())

                    if (responseData.success) {
                        _statusMessage.value = "Location sent successfully"
                    } else {
                        _errorMessage.value = "Error: ${responseData.success}"
                    }
                } else {
                    _errorMessage.value = "Failed to send location: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.localizedMessage}"
            }
        })


    }

    fun sendUserSearch(locationData: LocationByStateData) {
        _isLoading.value = true
        _statusMessage.value = "Sending location..."

        RetrofitInstance.apiInterface.sendUserSearch(locationData).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                _isLoading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    _responseData.value = responseData
                    _centerList.value = responseData.centersList
                    Log.i("LiveData",_centerList.toString())
                    Log.i("Centers1", responseData.centersList.toString())

                    if (responseData.success) {
                        _statusMessage.value = "Location sent successfully"
                    } else {
                        _errorMessage.value = "Error: ${responseData.success}"
                    }
                } else {
                    _errorMessage.value = "Failed to send location: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.localizedMessage}"
            }
        })


    }
}