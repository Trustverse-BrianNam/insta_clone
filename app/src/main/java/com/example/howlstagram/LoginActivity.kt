package com.example.howlstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? =null
    var GOOGLE_LOGIN_CODE=9001//구글 로그인 시 request code
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener{
            signinAdnSignup()
        }
        google_sign_in_button.setOnClickListener{//구글 로그인 버튼에 만든 function을 넣어 줌
            //First step
            googleLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))//google api 키를 넣어줌
            .requestEmail()//email 아이디를 받아옴
            .build()//닫아줌
        googleSignInClient = GoogleSignIn.getClient(this,gso)//해당 옵션 값을 googleSignInClient에 세팅 해 준다.
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)//siginInIntent 와 request code 넣어줌
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ff","ff")

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d("ff","ff")

            if(result!!.isSuccess){
                var account = result.signInAccount
                //Second step

                firebaseAuthWithGoogle(account)
            }
        }
    }

    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){

        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{//회원가입한 결과값을 받아옴
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()

                }
            }
    }

    fun signinAdnSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener{//회원가입한 결과값을 받아옴
                task ->
                    if(task.isSuccessful){//id가 생성되었을 때 작동하는 if
                        //id가 생성되었을 때 필요한 코드를 입력하는 부
                        moveMainPage(task.result?.user)//id 생성 성공적일 때 move mainpage 함
                    }else if(!task.exception?.message.isNullOrEmpty()){
                        //Show the error message
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }else{
                        //Login if you have account
                        signinEmail()//id 호출도 아니고 에러도 아닐때 일반 로그인 함
                    }
        }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener{//회원가입한 결과값을 받아옴
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()

                }
            }
    }

    fun moveMainPage(user: FirebaseUser?){//login 성공 시 다음 페이지로 넘어 가는 함수
        if(user !=null){//fire base user상태가 있을 경우 다음페이지로 넘어감
            startActivity(Intent(this,MainActivity::class.java))//mainactivity를 호출 하는 코드

        }

    }
}