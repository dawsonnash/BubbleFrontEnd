import com.example.bubblefrontend.LoginRequest
import com.example.bubblefrontend.LoginResponse
import com.example.bubblefrontend.RegistrationRequest
import com.example.bubblefrontend.RegistrationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiMethods {
    @POST("login")
    fun authenticateLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body registrationRequest: RegistrationRequest): Call<RegistrationResponse>

}

