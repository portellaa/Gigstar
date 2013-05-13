package org.ua.gigstar;

import org.ua.gigstar.utils.GigCustomActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GigAboutActivity extends GigCustomActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.aboutmain, getResources().getString(R.string.aboutTabName));
        
        if (this.getIntent().getStringExtra("backActiv") != null) {
        	getLeftButton().setText(this.getIntent().getStringExtra("backActiv"));
        }
        else getLeftButton().setText(getResources().getString(R.string.moreTabName));
        getLeftButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
        getLeftButton().setEnabled(true);
        getLeftButton().setVisibility(Button.VISIBLE);
	}
}
