package com.blocktechwh.app.block.Activity.Actions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.blocktechwh.app.block.Bean.VoteInfo;
import com.blocktechwh.app.block.Common.Urls;
import com.blocktechwh.app.block.R;
import com.blocktechwh.app.block.Utils.CallBack;
import com.blocktechwh.app.block.Utils.HttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class StartActivity extends AppCompatActivity {

    private String theme;//主题
    private String themeImg;//主题图片

    private String voteFee;//奖金总额
    private String content;//投票活动描述
    private String expireTime;//投票到期时间

    private TextView tv_to_add;

    private TextView addPlayersButton;
    private Switch sIfSingle;
    private Switch sIfNoSee;
    private Switch switchifAdd;
    private boolean isLimited=false;
    private boolean isAnonymous=true;
    private boolean isRaise=true;

    private TextView tvPopupTime;
    private LinearLayout layout;
    private boolean isFold=true;//判断是否显示
    private TextView tv_main=null;//遮罩层
    private PopupWindow taxWindow;// 弹出框  
    private Button btnStartVote;
    private EditText etTheme;
    private TextView tvAddReward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_vote);
        //addPlayersButton=(TextView)findViewById(R.id.textView17);

        initData();
        addEvent();


    }
    private void initData(){
        LinearLayout ll=(LinearLayout) findViewById(R.id.ll_active_box);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_vote_active, null);
        ll.addView(view);
        addPlayersButton=(TextView) findViewById(R.id.tv_to_add);
        tv_to_add=(TextView) findViewById(R.id.tv_to_add);
        sIfSingle=(Switch) findViewById(R.id.switch_vote);
        sIfNoSee=(Switch) findViewById(R.id.switch_vote2);
        switchifAdd=(Switch) findViewById(R.id.switchifAdd);
        tvAddReward=(TextView) findViewById(R.id.tvAddReward);
        sIfSingle.setChecked(false);
        sIfNoSee.setChecked(true);
        switchifAdd.setChecked(true);


        tvPopupTime=(TextView) findViewById(R.id.tv_popup_time);
        layout=(LinearLayout) findViewById(R.id.layout_main);
        btnStartVote=(Button) findViewById(R.id.btnStartVote);
        etTheme=(EditText) findViewById(R.id.etTheme);
        VoteInfo.setIsLimited(isLimited);
        VoteInfo.setIsAnonymous(isAnonymous);
        VoteInfo.setIsRaise(isRaise);

        //投票人员头像
        GridView gridview = (GridView) findViewById(R.id.gridview_voters);
        gridview.setAdapter(new ImageAdapter(this));



        if(VoteInfo.getVoteTheme()!=null){
            etTheme.setText(VoteInfo.getVoteTheme().toString());
        }
        //etTheme.setText(VoteInfo.getVoteTheme().toString());

    }

    private void addEvent(){//tvPopupTime
        //点击开关选择是否单选
        sIfSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sIfSingle.setText("单选");
                    isLimited=true;
                }else{
                    sIfSingle.setText("多选");
                    isLimited=false;
                }
                VoteInfo.setIsLimited(isLimited);
                System.out.print(VoteInfo.getIsLimited());

            }
        });

        //点击弹出时间选择器
        tvPopupTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isFold){
                    isFold=false;
                    showTaxDetail(view);
                    tv_main=new TextView(StartActivity.this);
                    tv_main.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
                    tv_main.setBackgroundColor(Color.parseColor("#66000000"));
                    tv_main.setClickable(true);
                    tv_main.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isFold=true;
                            taxWindow.dismiss();
                            layout.removeView(tv_main);
                        }
                    });

                }else{
                    isFold=true;
                    taxWindow.dismiss();
                    layout.removeView(tv_main);
                }
            }
        });

        //点击跳转设置奖励资金页面
        tvAddReward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,SetRewardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });

        //点击开关选择是否匿名
        sIfNoSee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sIfNoSee.setText("开");
                    isAnonymous=true;
                }else{
                    sIfNoSee.setText("关");
                    isAnonymous=false;
                }
                VoteInfo.setIsLimited(isAnonymous);
                System.out.print(VoteInfo.getIsAnonymous());
            }
        });

        //点击开关选择是否匿名
        switchifAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchifAdd.setText("开");
                    isRaise=true;
                }else{
                    switchifAdd.setText("关");
                    isRaise=false;
                }
                VoteInfo.setIsRaise(isRaise);

            }
        });


        addPlayersButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String theme=etTheme.getText().toString();
                VoteInfo.setVoteTheme(theme);
                Intent intent = new Intent(StartActivity.this,AddPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);

            }
        });
        tv_to_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,AddPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);

            }
        });



        //点击提交发起投票
        btnStartVote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                List<Integer>voterList=new ArrayList<>();
                voterList.add(2);
                voterList.add(1);
                List<Integer>rewardRuleList=new ArrayList<>();
                rewardRuleList.add(80);
                rewardRuleList.add(20);

                json.put("voteImg","sduhfciusdaghhfi329485yhdbngiuvywiohf");
//                if(VoteInfo.getVoteTheme().equals(null)){
//                    json.put("voteTheme",etTheme.getText().toString());
//                }else{
//                    json.put("voteTheme",VoteInfo.getVoteTheme());
//
//                }
                json.put("voteTheme",etTheme.getText().toString());
                json.put("isLimited",VoteInfo.getIsLimited());
                json.put("isRaise",VoteInfo.getIsRaise());
                json.put("isAnonymous",VoteInfo.getIsAnonymous());
                json.put("voteFee",100.00);//VoteInfo.getVoteFee()
                json.put("voteExpireTime","2017-11-16T03:52:17.106Z");
                json.put("options",VoteInfo.getOptions());
                json.put("voteTarget",voterList);//VoteInfo.getVoterList()
                json.put("voteRewardRule",rewardRuleList);//VoteInfo.getVoteRewardRule()

                System.out.print("json="+json);
                HttpClient.post(this, Urls.MakeVote, json.toString(), new CallBack<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        Toast.makeText(StartActivity.this,"发起投票成功",Toast.LENGTH_SHORT).show();;
                    }
                });

            }
        });

    }
    private void showTaxDetail(View view){
        LayoutInflater inflater=LayoutInflater.from(this);
        // 加载弹出框的布局
        View contentView=inflater.inflate(R.layout.view_time_popup, null);
        contentView.measure(0,0);
        taxWindow=new PopupWindow(contentView,contentView.getMeasuredWidth(),contentView.getMeasuredHeight(),true);
        taxWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        taxWindow.setOutsideTouchable(true);
        taxWindow.setFocusable(false);
        int[] location = new int[2];
        // 得到按钮控件的坐标，便于定位弹出框位置
        tvPopupTime.getLocationInWindow(location);
        int taxWindowWidth=taxWindow.getContentView().getMeasuredWidth();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        taxWindow.showAtLocation(tvPopupTime, Gravity.NO_GRAVITY,(screenWidth-taxWindowWidth)/2,location[1]+95);
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;

        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // Keep all Images in array
        public Integer[] mThumbIds = {
                R.mipmap.icon25,R.mipmap.ic_launcher,R.mipmap.icon24,R.mipmap.icon25,R.mipmap.icon25,R.mipmap.ic_launcher,R.mipmap.icon24,R.mipmap.icon25,R.mipmap.icon25,R.mipmap.ic_launcher,R.mipmap.icon24,R.mipmap.icon25
        };
    }
}
