package com.wy.flowlayoutusedemo;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wy.flowlayoutusedemo.util.SharedPreferencesUtil;
import com.wy.flowlayoutusedemo.util.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WY on 2018/7/25.
 * 本地历史记录
 */
public class Button1Activity extends AppCompatActivity {

    @BindView(R.id.tv_titlebar_center)
    TextView tvTitlebarCenter;
    @BindView(R.id.iv_titlebar_left)
    ImageView ivTitlebarLeft;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.flowlayout_history)
    TagFlowLayout flowlayoutHistory;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.ll_history)
    LinearLayout llHistory;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button1);
        ButterKnife.bind(this);
        context = this;

        //测试数据
        String testData = "活着wy1234567890wyISBNwy中华人民共和国wy唉wysfsfsfswy1wy搜索wy尴尬啦wy活着啊";
        SharedPreferencesUtil.putString(context, "search_history", testData);

        initUI();
        initHistory();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        //下划线
        tvClear.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick({R.id.iv_titlebar_left, R.id.iv_search, R.id.tv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_titlebar_left://返回
                finish();
                break;
            case R.id.iv_search://搜索
                initSearch();
                break;
            case R.id.tv_clear://清除历史记录
                SharedPreferencesUtil.putString(context, "search_history", "");
                initHistory();
                break;
        }
    }

    /**
     * 初始化历史记录
     */
    private void initHistory() {
        final List<String> readHistory = readHistory();
        if (readHistory != null && readHistory.size() > 0) {
            llHistory.setVisibility(View.VISIBLE);
        } else {
            llHistory.setVisibility(View.GONE);
        }

        //为FlowLayout填充数据
        flowlayoutHistory.setAdapter(new TagAdapter(readHistory) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {

                TextView view = (TextView) View.inflate(context, R.layout.flowlayout_textview, null);
                view.setText(readHistory.get(position));
                return view;
            }
        });

        //为FlowLayout的标签设置监听事件
        flowlayoutHistory.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                ToastUtil.makeText(context, readHistory.get(position));
                etSearch.setText(readHistory.get(position));
                etSearch.setSelection(readHistory.get(position).length());
                return true;
            }
        });
    }

    /**
     * 从SP中读取历史记录
     */
    private List<String> readHistory() {
        List<String> readHistoryList = new ArrayList<>();
        String search_history = SharedPreferencesUtil.getString(context, "search_history", null);

        //将String转为List
        if (!TextUtils.isEmpty(search_history)) {
            String[] strs = search_history.split("wy");
            for (int i = 0; i < strs.length; i++) {
                readHistoryList.add(i, strs[i]);
            }
        }

        return readHistoryList;
    }

    /**
     * 将历史记录写入到SP中
     */
    private void writeHistory(String write) {
        if (TextUtils.isEmpty(write)) {
            return;
        }

        String writeHistory = "";
        //获取历史记录
        List<String> readHistoryList = readHistory();

        //如果不重复，则添加为第一个历史记录；
        //如果重复，则删除已有，再添加为第一个历史记录；
        for (int i = 0; i < readHistoryList.size(); i++) {
            boolean hasWrite = readHistoryList.get(i).equals(write);
            if (hasWrite) {
                readHistoryList.remove(i);
                break;
            }
        }
        readHistoryList.add(0, write);

        //历史记录最多为10个
        if (readHistoryList.size() > 10) {
            readHistoryList = readHistoryList.subList(0, 10);
        }

        //将ArrayList转为String
        for (int i = 0; i < readHistoryList.size(); i++) {
            writeHistory += readHistoryList.get(i) + "wy";
        }
        SharedPreferencesUtil.putString(context, "search_history", writeHistory);
    }

    /**
     * 初始化搜索
     */
    private void initSearch() {
        String inputSearch = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(inputSearch)) {
            ToastUtil.makeText(context, "请输入要查询的条码");
            return;
        }
        writeHistory(inputSearch);
        initHistory();
    }
}
