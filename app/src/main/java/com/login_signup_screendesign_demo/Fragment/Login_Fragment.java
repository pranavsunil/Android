package com.login_signup_screendesign_demo.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.login_signup_screendesign_demo.API.LoginApi;
import com.login_signup_screendesign_demo.CustomToast;
import com.login_signup_screendesign_demo.Model.BodyLoginUser;
import com.login_signup_screendesign_demo.Model.ResponseLoginUser;
import com.login_signup_screendesign_demo.R;
import com.login_signup_screendesign_demo.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login_Fragment extends Fragment implements OnClickListener {
	private static View view;

	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;
	private static ProgressBar progressBar;

	Retrofit retrofit;
	LoginApi loginApi;
	public Login_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_layout, container, false);
		retrofit = new Retrofit.Builder()
				.baseUrl("https://www.innerworkindia.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		loginApi = retrofit.create(LoginApi.class);
		initViews();
		setListeners();

		return view;

	}


	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);
		progressBar = (ProgressBar) view.findViewById(R.id.progresBarLogin);
		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

		// Setting text selector over textviews

	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			checkValidation();
			break;

		case R.id.forgot_password:

			// Replace forgot password fragment with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer,
							new ForgotPassword_Fragment(),
							Utils.ForgotPassword_Fragment).commit();
			break;
		case R.id.createAccount:

			// Replace signup frgament with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer, new SignUp_Fragment(),
							Utils.SignUp_Fragment).commit();
			break;
		}

	}

	// Check Validation before login
	private void checkValidation() {
		// Get email id and password
		String getEmailId = emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		Pattern p = Pattern.compile(Utils.regEx);

		Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
		// Else do login and do your stuff
		progressBar.setVisibility(View.VISIBLE);
		Call<ResponseLoginUser> call = loginApi.logInUser(new BodyLoginUser(emailid.getText().toString(), password.getText().toString()));
		call.enqueue(new Callback<ResponseLoginUser>() {
			@Override
			public void onResponse(Call<ResponseLoginUser> call, Response<ResponseLoginUser> response) {
				if(!response.isSuccessful()){
					progressBar.setVisibility(View.GONE);
					new CustomToast().Show_Toast(getActivity(), view,
							response.message());
					//Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
					return;
				}

				ResponseLoginUser responseLoginUser = response.body();
				progressBar.setVisibility(View.GONE);
				if(responseLoginUser.getCode() != 200){
					new CustomToast().Show_Toast(getActivity(), view,
							responseLoginUser.getMessage());
					//Toast.makeText(getContext(), responseLoginUser.getMessage(), Toast.LENGTH_SHORT).show();
					return;
				}
				new CustomToast().Show_Toast(getActivity(), view,
						"Received token: " + responseLoginUser.getToken());
				//Toast.makeText(getContext(), "Received token: " + responseLoginUser.getToken(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Call<ResponseLoginUser> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				Log.i("LoginFragment", "onFailure: " + t.getLocalizedMessage());
				new CustomToast().Show_Toast(getActivity(), view,
						"Login Failed. " + t.getLocalizedMessage());
				//Toast.makeText(getContext(), "Login Failed. " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
		});
		/*else
			Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT)
					.show();*/

	}
}
