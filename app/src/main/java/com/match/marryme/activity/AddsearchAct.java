package com.match.marryme.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityAddsearchBinding;
import com.match.marryme.utils.DefaultValue;
import com.match.marryme.utils.StringUtil;

public class AddsearchAct extends Activity implements View.OnClickListener {

    ActivityAddsearchBinding binding;

    String scdata = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_addsearch);

        binding.flBtnBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.llReligion.setOnClickListener(this);
        binding.llMarriage.setOnClickListener(this);
        binding.llSalary.setOnClickListener(this);
        binding.llProperty.setOnClickListener(this);
        binding.llBloodtype.setOnClickListener(this);
        binding.llEdu.setOnClickListener(this);
        binding.llSmoke.setOnClickListener(this);
        binding.llDrinking.setOnClickListener(this);
        binding.llChildren.setOnClickListener(this);

        binding.tvRegImage.setOnClickListener(this);

        binding.btnSearch.setOnClickListener(this);


        String data = getIntent().getStringExtra("data");

        if (!StringUtil.isNull(data)) {

            String[] asItem = data.split("#");

            String reli = asItem[0].replace("AS1:", "");
            binding.tvReligion.setText(reli.replaceAll("\\|", ","));

            String ann = asItem[1].replace("AS2:", "");
            binding.tvSalary.setText(ann);

            String prop = asItem[2].replace("AS3:", "");
            binding.tvProperty.setText(prop);

            String blood = asItem[3].replace("AS4:", "");
            binding.tvBloodtype.setText(blood.replaceAll("\\|", ","));

            String edu = asItem[4].replace("AS5:", "");
            binding.tvEdu.setText(edu.replaceAll("\\|", ","));

            String smoke = asItem[5].replace("AS6:", "");
            binding.tvSmoke.setText(smoke);

            String drink = asItem[6].replace("AS7:", "");
            binding.tvDrinking.setText(drink);

            String child = asItem[7].replace("AS8:", "");
            binding.tvChildren.setText(child);

            String image = asItem[8].replace("AS9:", "");
            if(image.equalsIgnoreCase("Y")) {
                binding.tvRegImage.setSelected(true);
            }

            String marriage = asItem[9].replace("AS10:", "");
            if(!StringUtil.isNull(marriage)) {
                if(marriage.equalsIgnoreCase("marry")) {
                    binding.tvMarriage.setText("결혼");
                } else if(marriage.equalsIgnoreCase("remarry")) {
                    binding.tvMarriage.setText("재혼");
                } else if(marriage.equalsIgnoreCase("friend")) {
                    binding.tvMarriage.setText("재혼");
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        String result = data.getStringExtra("data");

        switch (requestCode) {
            case DefaultValue.ADDRELIGION:
                if(result.replaceAll("\\|", "").equalsIgnoreCase("전체")) {
                    binding.tvReligion.setText(null);
                } else {
                    binding.tvReligion.setText(result.replaceAll("\\|", ","));
                }
                break;
            case DefaultValue.ADDSALARY:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvSalary.setText(null);
                } else {
                    binding.tvSalary.setText(result);
                }
                break;
            case DefaultValue.ADDPROPERTY:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvProperty.setText(null);
                } else {
                    binding.tvProperty.setText(result);
                }
                break;
            case DefaultValue.ADDBLOOD:
                if(result.replaceAll("\\|", "").equalsIgnoreCase("전체")) {
                    binding.tvBloodtype.setText(null);
                } else {
                    binding.tvBloodtype.setText(result.replaceAll("\\|", ","));
                }
                break;
            case DefaultValue.ADDEDU:
                if(result.replaceAll("\\|", "").equalsIgnoreCase("전체")) {
                    binding.tvEdu.setText(null);
                } else {
                    binding.tvEdu.setText(result.replaceAll("\\|", ","));
                }
                break;
            case DefaultValue.ADDSMOKE:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvSmoke.setText(null);
                } else {
                    binding.tvSmoke.setText(result);
                }
                break;
            case DefaultValue.ADDDRINKING:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvDrinking.setText(null);
                } else {
                    binding.tvDrinking.setText(result);
                }
                break;
            case DefaultValue.ADDCHILD:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvChildren.setText(null);
                } else {
                    binding.tvChildren.setText(result);
                }
                break;

            case DefaultValue.ADDMARRIAGE:
                if(result.equalsIgnoreCase("전체")) {
                    binding.tvMarriage.setText(null);
                } else {
                    binding.tvMarriage.setText(result);
                }
                break;
        }
    }

    private void initFilter() {
        binding.tvReligion.setText("전체");
        binding.tvSalary.setText("전체");
        binding.tvProperty.setText("전체");
        binding.tvBloodtype.setText("전체");
        binding.tvEdu.setText("전체");
        binding.tvSmoke.setText("전체");
        binding.tvDrinking.setText("전체");
        binding.tvChildren.setText("전체");
//        binding.llProfimg.setSelected(false);
//        binding.llMrelation.setSelected(false);
//        binding.llIdentityverify.setSelected(false);
        binding.tvRegImage.setSelected(false);
//        binding.llJobinfo.setSelected(false);
//        binding.llRegsalary.setSelected(false);
//        binding.llRegedu.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_btn_back:
                finish();
                break;
            case R.id.ll_home:
//                startActivity(new Intent(this, MainActivity.class));
//                finishAffinity();
                finish();
                break;
            case R.id.ll_religion:
                Intent religionIntent = new Intent(this, ListDlgAct.class);
//                Intent religionIntent = new Intent(this, MultipleListDlgAct.class);
//                religionIntent.putExtra("subject", "add_religion");
//                religionIntent.putExtra("select", binding.tvReligion.getText().toString().replaceAll(",", "\\|"));
                religionIntent.putExtra("subject", "religion2");
                religionIntent.putExtra("select", binding.tvReligion.getText());
                startActivityForResult(religionIntent, DefaultValue.ADDRELIGION);
                break;
            case R.id.ll_salary:
                Intent salaryIntent = new Intent(this, ListDlgAct.class);
                salaryIntent.putExtra("subject", "annual2");
                salaryIntent.putExtra("select", binding.tvSalary.getText());
                startActivityForResult(salaryIntent, DefaultValue.ADDSALARY);
                break;
            case R.id.ll_property:
                Intent propertyIntent = new Intent(this, ListDlgAct.class);
//                propertyIntent.putExtra("subject", "add_property");
//                propertyIntent.putExtra("select", binding.tvProperty.getText());
                propertyIntent.putExtra("subject", "property2");
                propertyIntent.putExtra("select", binding.tvProperty.getText());
                startActivityForResult(propertyIntent, DefaultValue.ADDPROPERTY);
                break;
            case R.id.ll_bloodtype:
//                Intent bloodIntent = new Intent(this, MultipleListDlgAct.class);
//                bloodIntent.putExtra("subject", "add_bloodtype");
//                bloodIntent.putExtra("select", binding.tvBloodtype.getText().toString().replaceAll(",", "\\|"));
                Intent bloodIntent = new Intent(this, ListDlgAct.class);
                bloodIntent.putExtra("subject", "blood2");
                bloodIntent.putExtra("select", binding.tvBloodtype.getText());
                startActivityForResult(bloodIntent, DefaultValue.ADDBLOOD);
                break;
            case R.id.ll_edu:
//                Intent eduIntent = new Intent(this, MultipleListDlgAct.class);
//                eduIntent.putExtra("subject", "add_edu");
//                eduIntent.putExtra("select", binding.tvEdu.getText().toString().replaceAll(",", "\\|"));
                Intent eduIntent = new Intent(this, ListDlgAct.class);
                eduIntent.putExtra("subject", "edu2");
                eduIntent.putExtra("select", binding.tvEdu.getText());
                startActivityForResult(eduIntent, DefaultValue.ADDEDU);
                break;
            case R.id.ll_smoke:
                Intent smokeIntent = new Intent(this, ListDlgAct.class);
                smokeIntent.putExtra("subject", "add_smoke");
                smokeIntent.putExtra("select", binding.tvSmoke.getText());
                startActivityForResult(smokeIntent, DefaultValue.ADDSMOKE);
                break;
            case R.id.ll_drinking:
                Intent drinkingIntent = new Intent(this, ListDlgAct.class);
                drinkingIntent.putExtra("subject", "add_drinking");
                drinkingIntent.putExtra("select", binding.tvDrinking.getText());
                startActivityForResult(drinkingIntent, DefaultValue.ADDDRINKING);
                break;
            case R.id.ll_children:
                Intent childrenIntent = new Intent(this, ListDlgAct.class);
                childrenIntent.putExtra("subject", "add_children");
                childrenIntent.putExtra("select", binding.tvChildren.getText());
                startActivityForResult(childrenIntent, DefaultValue.ADDCHILD);
                break;

            case R.id.ll_marriage:
                Intent marriageIntent = new Intent(this, ListDlgAct.class);
                marriageIntent.putExtra("subject", "add_marriage");
                marriageIntent.putExtra("select", binding.tvMarriage.getText());
                startActivityForResult(marriageIntent, DefaultValue.ADDMARRIAGE);
                break;

            case R.id.tv_reg_image:
                binding.tvRegImage.setSelected(!binding.tvRegImage.isSelected());
                break;

            case R.id.btn_search:

                scdata += "AS1:" + binding.tvReligion.getText().toString().replaceAll(",", "\\|") + "#";
                scdata += "AS2:" + binding.tvSalary.getText().toString() + "#";
                scdata += "AS3:" + binding.tvProperty.getText() + "#";
                scdata += "AS4:" + binding.tvBloodtype.getText().toString().replaceAll(",", "\\|") + "#";
                scdata += "AS5:" + binding.tvEdu.getText().toString().replaceAll(",", "\\|") + "#";
                scdata += "AS6:" + binding.tvSmoke.getText() + "#";
                scdata += "AS7:" + binding.tvDrinking.getText() + "#";
                scdata += "AS8:" + binding.tvChildren.getText() + "#";

                if(binding.tvRegImage.isSelected()) {
                    scdata += "AS9:" + "Y" + "#";
                } else {
                    scdata += "AS9:" + "" + "#";
                }

                if(!StringUtil.isNull(binding.tvMarriage.getText().toString())) {
                    if(binding.tvMarriage.getText().toString().equalsIgnoreCase("결혼"))  {
                        scdata += "AS10:" + "marry";
                    } else if(binding.tvMarriage.getText().toString().equalsIgnoreCase("재혼")){
                        scdata += "AS10:" + "remarry";
                    } else if(binding.tvMarriage.getText().toString().equalsIgnoreCase("재혼")) {
                        scdata += "AS10:" + "friend";
                    }
                } else {
                    scdata += "AS10:";
                }

                Log.e("TEST_HOME", "scdata: " + scdata);

                Intent result = new Intent();
                result.putExtra("data", scdata);
                setResult(RESULT_OK, result);
                finish();
                break;
        }
    }
}
