package com.fangdai.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fangdai.R;
import com.fangdai.listener.ShangYeSpinnerSelectedListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("ValidFragment")
public class ShangDaiFragment extends Fragment {
	private static final String[] nx = { "1", "2", "3", "4", "5", "10", "15",
			"20", "25", "30" };
	private static final String[] lv = { "最新基准利率7折", "最新基准利率7.5折", "最新基准利率8折",
			"最新基准利率8.5折", "最新基准利率9折", "最新基准利率9.5折", "最新基准利率", "最新基准利率1.1倍",
			"最新基准利率1.2倍", "最新基准利率1.3倍" };
	private EditText shangyelilv;
	private EditText zevj1;
	private Spinner nxvj1, lvv1;
	private Button subj1;
	private TextView am10;
	private TextView am11;
	private TextView am12;
	private TextView am13;
	private TextView am14;
	private double lilv = 5.9;
	private LinearLayout gongjijinlilv_layout;
	private int fangshi = 0;
	private RadioGroup radioGroup;
	private PieChart mChart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gongjijin, container, false);
		initMyView(view);
		return view;
	}

	private void initMyView(View view) {
		init(view);
		clear();
		zevj1 = (EditText) view.findViewById(R.id.zev1);
		nxvj1 = (Spinner) view.findViewById(R.id.nxv1);
		lvv1 = (Spinner) view.findViewById(R.id.lvv1);
		shangyelilv = (EditText) view.findViewById(R.id.shangyelilv);
		initPieChart(view);
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.radioBenjin) {
							fangshi = 1;
						} else {
							fangshi = 0;
						}
					}
				});
		gongjijinlilv_layout = (LinearLayout) view
				.findViewById(R.id.gongjijinlilv_layout);
		gongjijinlilv_layout.setVisibility(View.GONE);
		shangyelilv.setText(String.valueOf(5.9));
		subj1 = (Button) view.findViewById(R.id.sub1);
		// 将可选内容与ArrayAdapter连接起来
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, nx);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		nxvj1.setAdapter(adapter);
		nxvj1.setSelection(7, true);
		// 设置默认值
		nxvj1.setVisibility(View.VISIBLE);
		// 将可选内容与ArrayAdapter连接起来
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, lv);
		// 设置下拉列表的风格
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		lvv1.setAdapter(adapter1);
		lvv1.setSelection(6, true);
		// 设置默认值
		lvv1.setVisibility(View.VISIBLE);
		// 添加事件Spinner事件监听
		lvv1.setOnItemSelectedListener(new ShangYeSpinnerSelectedListener(
				shangyelilv, lilv));
		this.subj1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				JiSuan();
			}

		});
		JiSuan();
	}

	private void initPieChart(View view) {
		mChart = (PieChart) view.findViewById(R.id.chart1);
		Description d=new Description();
		d.setText("本息占比");
		mChart.setDescription(d);
//		mChart.setDrawYValues(true);
		mChart.setDrawHoleEnabled(false);
		mChart.setRotationAngle(0);
//		mChart.setDrawXValues(true);
		mChart.setRotationEnabled(false);
		mChart.setUsePercentValues(true);
		setData(1, 100, 0, 0, 0);
		mChart.animateXY(1500, 1500);
	}

	private void JiSuan() {
		Editable value = zevj1.getText();
		if (value.toString() == null || value.toString().length() <= 0) {
			Toast.makeText(getActivity(), "贷款总额不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Editable value1 = shangyelilv.getText();
		if (value1.toString() == null || value1.toString().length() <= 0) {
			Toast.makeText(getActivity(), "利率不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		double ze = Double.parseDouble(value.toString()) * 10000;
		double nx = Double.parseDouble(nxvj1.getSelectedItem().toString()) * 12;
		double rate = Double.parseDouble(shangyelilv.getText().toString()) / 100;

		cal(ze, nx, rate);
		setData(1, 100, ze, nx, rate);
	}

	private void setData(int count, float range, double ze, double nx,
			double rate) {
		double zem = (ze * rate / 12 * Math.pow((1 + rate / 12), nx))
				/ (Math.pow((1 + rate / 12), nx) - 1);
		double amount = zem * nx;
		double rateAmount = amount - ze;
		List<PieEntry> yVals1 = new ArrayList<PieEntry>();
		yVals1.add(new PieEntry((float) (ze / amount), 0));
		yVals1.add(new PieEntry((float) (rateAmount / amount), 1));
		ArrayList<String> xVals = new ArrayList<String>();
		xVals.add("贷款本金");
		xVals.add("贷款利息");
		PieDataSet set1 = new PieDataSet(yVals1, "");
		set1.setSliceSpace(3f);
		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(ColorTemplate.getHoloBlue());
		colors.add(ColorTemplate.COLORFUL_COLORS[2]);
		set1.setColors(colors);
		PieData data = new PieData( set1);
		mChart.setData(data);
		mChart.highlightValues(null);
		mChart.animateXY(1500, 1500);
		mChart.invalidate();
	}

	public void cal(double ze, double nx, double rate) {
		double zem = (ze * rate / 12 * Math.pow((1 + rate / 12), nx))
				/ (Math.pow((1 + rate / 12), nx) - 1);
		double amount = zem * nx;
		double rateAmount = amount - ze;

		BigDecimal zemvalue = new BigDecimal(zem);
		double zemval = zemvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		BigDecimal amountvalue = new BigDecimal(amount);
		double amountval = amountvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		BigDecimal rateAmountvalue = new BigDecimal(rateAmount);
		double rateAmountval = rateAmountvalue.setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();

		double benjinm = ze / nx;
		double lixim = ze * (rate / 12);
		double diff = benjinm * (rate / 12);
		double huankuanm = benjinm + lixim;
		double zuihoukuan = diff + benjinm;
		double av = (huankuanm + zuihoukuan) / 2;
		double zong = av * nx;
		double zongli = zong - ze;

		BigDecimal huankuanmvalue = new BigDecimal(huankuanm);
		double huankuanmval = huankuanmvalue.setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();

		BigDecimal diffvalue = new BigDecimal(diff);
		double diffmval = diffvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		BigDecimal zongvalue = new BigDecimal(zong);
		double zongval = zongvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		BigDecimal zonglivalue = new BigDecimal(zongli);
		double zonglival = zonglivalue.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		if (fangshi == 0) {
			am10.setText((new BigDecimal(ze / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
			am11.setText(nx + "月");
			am12.setText(zemval + "元");
			am13.setText((new BigDecimal(rateAmountval / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
			am14.setText((new BigDecimal(amountval / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
		} else {
			am10.setText((new BigDecimal(ze / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
			am11.setText(nx + "月");
			am12.setText("首月" + huankuanmval + ",月减" + diffmval);
			am13.setText((new BigDecimal(zonglival / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
			am14.setText((new BigDecimal(zongval / 10000)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue()
					+ "万元");
		}
	}

	public void init(View view) {
		am10 = (TextView) view.findViewById(R.id.am10);
		am11 = (TextView) view.findViewById(R.id.am11);
		am12 = (TextView) view.findViewById(R.id.am12);
		am13 = (TextView) view.findViewById(R.id.am13);
		am14 = (TextView) view.findViewById(R.id.am14);
	}

	public void clear() {
		am10.setText(0 + "万元");
		am11.setText(0 + "月");
		am12.setText(0 + "元");
		am13.setText(0 + "万元");
		am14.setText(0 + "万元");
	}

}
