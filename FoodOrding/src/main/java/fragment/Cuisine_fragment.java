package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import activity.ChineseFoodActivity;
import activity.KoreaFoodActivity;
import activity.WesternFoodActivity;
import cn.gdin.hk.hungry.R;

public class Cuisine_fragment extends Fragment implements OnClickListener {
    private View view;
    private ImageView iv_zhongcan,iv_xican, iv_korea;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private LinearLayout cuisine_fragment_ln;
    private Cuisine_fragment fragment;
    private LinearLayout ln_serach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = LayoutInflater.from(getActivity()).inflate(R.layout.firststep_cuisine_fragment, null);
        iv_zhongcan = (ImageView) view.findViewById(R.id.iv_zhongcan);
        iv_xican = (ImageView) view.findViewById(R.id.iv_xican);
        iv_korea = (ImageView) view.findViewById(R.id.iv_korea);
        iv_zhongcan.setOnClickListener(this);
        iv_xican.setOnClickListener(this);
        iv_korea.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_zhongcan:
                Intent intent = new Intent(getActivity(), ChineseFoodActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_xican:
                Intent intent1 = new Intent(getActivity(), WesternFoodActivity.class);
                startActivity(intent1);
                break;
            case R.id.iv_korea:
                Intent intent2 = new Intent(getActivity(), KoreaFoodActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
