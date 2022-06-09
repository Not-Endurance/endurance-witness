package com.example.uhf.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.uhf.R;
import com.example.uhf.activity.UHFMainActivity;
import com.example.uhf.tools.UIHelper;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHF;

import static com.example.uhf.R.id.etPtr_filterK;


public class UHFKillFragment extends KeyDwonFragment implements OnClickListener {

    private static final String TAG = "UHFKillFragment";

    private UHFMainActivity mContext;

    CheckBox CkWithUii_Kill;
    EditText EtTagUii_Write;
    EditText EtAccessPwd_Kill;
    Button BtUii_Kill;
    Button btnKill;
    EditText etLen_Read;
    EditText etData_filter;
    EditText etPtr_Read;
    RadioButton rbEPC_filter;
    RadioButton rbTID_filter;
    RadioButton rbUser_filter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_kill_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (UHFMainActivity) getActivity();
        etPtr_Read = (EditText) getView().findViewById(etPtr_filterK);
        etLen_Read = (EditText) getView().findViewById(R.id.etLen_filterK);
        etData_filter = (EditText) getView().findViewById(R.id.etData_filterK);

        rbEPC_filter = (RadioButton) getView().findViewById(R.id.rbEPC_filterK);
        rbTID_filter = (RadioButton) getView().findViewById(R.id.rbTID_filterK);
        rbUser_filter = (RadioButton) getView().findViewById(R.id.rbUser_filterK);

        CkWithUii_Kill = (CheckBox) getView().findViewById(R.id.CkWithUii_Kill);


        EtAccessPwd_Kill = (EditText) getView().findViewById(R.id.EtAccessPwd_Kill);

        btnKill = (Button) getView().findViewById(R.id.btnKill);



        btnKill.setOnClickListener(new btnKillOnClickListener());
        rbEPC_filter.setOnClickListener(this);
        rbTID_filter.setOnClickListener(this);
        rbUser_filter.setOnClickListener(this);

    }

    public class BtUii_WriteClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            UHFTAGInfo uiiStr = mContext.mReader.inventorySingleTag();

            if (uiiStr != null) {
                EtTagUii_Write.setText(uiiStr.getEPC());
            } else {
                EtTagUii_Write.setText("");

                UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_tag_fail);
//                mContext.playSound(2);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.rbEPC_filter:
                if(rbEPC_filter.isChecked()){
                    etPtr_Read.setText("32");
                }
                break;
            case  R.id.rbTID_filter:
                if(rbTID_filter.isChecked()){
                    etPtr_Read.setText("0");
                }
                break;
            case  R.id.rbUser_filter:
                if(rbUser_filter.isChecked()){
                    etPtr_Read.setText("0");
                }
                break;

        }

    }

    public class btnKillOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            String strPWD = EtAccessPwd_Kill.getText().toString().trim();// 访问密码

            if (!TextUtils.isEmpty(strPWD)) {
                if (strPWD.length() != 8) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_addr_must_len8);
                    return;
                } else if (!mContext.vailHexInput(strPWD)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.rfid_mgs_error_nohex);

                    return;
                }
            } else {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nopwd);

                return;
            }

            if (CkWithUii_Kill.isChecked())// 指定标签
            {

                if(etPtr_Read.getText().toString()==null || etPtr_Read.getText().toString().isEmpty()){
                    UIHelper.ToastMessage(mContext, "过滤数据的起始地址不能为空");
                    return;
                }
                if(etData_filter.getText().toString()==null || etData_filter.getText().toString().isEmpty()){
                    UIHelper.ToastMessage(mContext, "过滤数据不能为空");
                    return;
                }
                if(etLen_Read.getText().toString()==null || etLen_Read.getText().toString().isEmpty()){
                    UIHelper.ToastMessage(mContext, "过滤数据长度不能为空");
                    return;
                }


                int filterPtr=Integer.parseInt(etPtr_Read.getText().toString());
                String filterData=etData_filter.getText().toString();
                int filterCnt=Integer.parseInt(etLen_Read.getText().toString());
                int filterBank=0;
                if(rbEPC_filter.isChecked()){
                    filterBank=IUHF.Bank_EPC;
                }else if(rbTID_filter.isChecked()){
                    filterBank=IUHF.Bank_TID;
                }else if(rbUser_filter.isChecked()){
                    filterBank=IUHF.Bank_USER;

                }

                String strUII = EtTagUii_Write.getText().toString().trim();
                if (TextUtils.isEmpty(strUII)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_tag_must_not_null);
                    return;
                }

                if (mContext.mReader.killTag(strPWD, filterBank,filterPtr,filterCnt,filterData)) {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_succ);
                      mContext.playSound(1);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_fail);
                    mContext.playSound(2);
                }

            } else {
                Boolean strKillUII = mContext.mReader.killTag(strPWD);
                if (strKillUII) {
                    UIHelper.ToastMessage(mContext, strKillUII + " "
                            + getString(R.string.rfid_mgs_kill_succ));
                    mContext.playSound(1);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_fail);
//                    mContext.playSound(2);
                    mContext.playSound(2);
                }
            }
        }
    }

    public class CkWithUii_WriteCheckedChangedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			EtTagUii_Write.setText("");

            if (isChecked) {
            	BtUii_Kill.setBackgroundResource(R.drawable.button_bg);
            	BtUii_Kill.setEnabled(true);
//                BtUii_Write.setVisibility(View.VISIBLE);
            } else {
            	BtUii_Kill.setBackgroundResource(R.drawable.button_bg_gray);
            	BtUii_Kill.setEnabled(false);
//                BtUii_Write.setVisibility(View.INVISIBLE);
            }
		}
    }

}
