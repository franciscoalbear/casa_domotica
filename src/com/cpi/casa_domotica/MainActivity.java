package com.cpi.casa_domotica;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		Button abrirPuertas = (Button)findViewById(R.id.abrirpuertas);
		Button encenderluces = (Button)findViewById(R.id.encenderluces);
		abrirPuertas.setOnClickListener(ejecutar);
		encenderluces.setOnClickListener(ejecutar);
		
	}
	
	private OnClickListener ejecutar = new OnClickListener(){

		@Override
		public void onClick(View view) {
			switch(view.getId()){
			    case R.id.abrirpuertas:
			    	lanzarPuertas(null);
			    	break;
			    case R.id.encenderluces:
			    	lanzarLuces(null);
			    	break;	
			
			}
		}
		
	};
	public void lanzarPuertas(View view){
		Intent i = new Intent(this,Abrir_puertas.class);
		startActivity(i);
	}
	
	public void lanzarLuces(View view){
		Intent i = new Intent(this,Luces.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
