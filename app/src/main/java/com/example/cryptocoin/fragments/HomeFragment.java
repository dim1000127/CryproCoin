package com.example.cryptocoin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cryptocoin.ActivityConvertCryptoValute;
import com.example.cryptocoin.R;
import com.example.cryptocoin.RetrofitSingleton;
import com.example.cryptocoin.cryptovalutepojo.CryptoValute;
import com.google.android.material.snackbar.Snackbar;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private Button buttonOpenConvertCryptoValute;
    private Subscription subscription;
    private SwipeRefreshLayout swipeRefreshLayoutHome;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayoutHome = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutHome);
        swipeRefreshLayoutHome.setOnRefreshListener(this);
        buttonOpenConvertCryptoValute = (Button) rootView.findViewById(R.id.btn_open_convert_cryptovalute);
        buttonOpenConvertCryptoValute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityConvertCryptoValute.class);
                startActivity(intent);
            }
        });
        getCryptoValuteData();
        return  rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onRefresh() {
        RetrofitSingleton.resetCryptoValuteObservable();
        getCryptoValuteData();
    }

    private void getCryptoValuteData(){
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        subscription = RetrofitSingleton.getCryptoValuteObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CryptoValute>() {
                    @Override
                    public void onCompleted() {
                        Log.d("onCompleted", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isAdded()) {
                            swipeRefreshLayoutHome.setRefreshing(false);
                            Snackbar.make(swipeRefreshLayoutHome, R.string.connection_error, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.try_again, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            swipeRefreshLayoutHome.setRefreshing(true);
                                            RetrofitSingleton.resetCryptoValuteObservable();
                                            getCryptoValuteData();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onNext(CryptoValute _cryptoValute) {
                        if (isAdded()) {
                            fillBlocksTopThree(_cryptoValute);
                            swipeRefreshLayoutHome.setRefreshing(false);
                        }
                    }
                });
    }

    /*private void APIGetPriceCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestsAPI requestsAPI = retrofit.create(RequestsAPI.class);

        Call<CryptoValute> dataCryptoValute = requestsAPI.getDataCryptoValute(1,3);

        dataCryptoValute.enqueue(new Callback<CryptoValute>() {
            @Override
            public void onResponse(Call<CryptoValute> call, Response<CryptoValute> response) {
                if(response.isSuccessful()){
                    CryptoValute dataCryptoValute = null;
                    dataCryptoValute = response.body();
                    fillBlocksTopThree(dataCryptoValute);
                    Log.d("List ", String.valueOf(response.body()));
                }
                else{
                    Log.d("Response code ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<CryptoValute> call, Throwable t) {
                Log.d("Failure", t.toString());
            }
        });
    }*/

    private void fillBlocksTopThree(CryptoValute dataCryptoValute){
        TextView textViewSymbolCryptoOne = getActivity().findViewById(R.id.symbolCryptoOne);
        textViewSymbolCryptoOne.setText(dataCryptoValute.getData().get(0).getSymbol());
        TextView textViewSymbolCryptoTwo = getActivity().findViewById(R.id.symbolCryptoTwo);
        textViewSymbolCryptoTwo.setText(dataCryptoValute.getData().get(1).getSymbol());
        TextView textViewSymbolCryptoThree = getActivity().findViewById(R.id.symbolCryptoThree);
        textViewSymbolCryptoThree.setText(dataCryptoValute.getData().get(2).getSymbol());
        TextView textViewPriceOne = getActivity().findViewById(R.id.priceCryptoValuteOne);
        textViewPriceOne.setText(String.format("$%.2f",dataCryptoValute.getData().get(0).getQuote().getUsdDataCoin().getPrice()));
        TextView textViewPriceTwo = getActivity().findViewById(R.id.priceCryptoValuteTwo);
        textViewPriceTwo.setText(String.format("$%.2f",dataCryptoValute.getData().get(1).getQuote().getUsdDataCoin().getPrice()));
        TextView textViewPriceThree = getActivity().findViewById(R.id.priceCryptoValuteThree);
        textViewPriceThree.setText(String.format("$%.2f",dataCryptoValute.getData().get(2).getQuote().getUsdDataCoin().getPrice()));
        double valueChange24hCryptoOne = dataCryptoValute.getData().get(0).getQuote().getUsdDataCoin().getPercentChange24h();
        TextView textViewChange24hCryptoOne = getActivity().findViewById(R.id.percent_change_24hCryptoOne);
        textViewChange24hCryptoOne.setText(String.format("%.2f%%", valueChange24hCryptoOne));
        if(valueChange24hCryptoOne>=0){ textViewChange24hCryptoOne.setTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null)); }
        else {textViewChange24hCryptoOne.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null)); }
        double valueChange24hCryptoTwo = dataCryptoValute.getData().get(1).getQuote().getUsdDataCoin().getPercentChange24h();
        TextView textViewChange24hCryptoTwo = getActivity().findViewById(R.id.percent_change_24hCryptoTwo);
        textViewChange24hCryptoTwo.setText(String.format("%.2f%%", valueChange24hCryptoTwo));
        if(valueChange24hCryptoTwo>=0){ textViewChange24hCryptoTwo.setTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null)); }
        else {textViewChange24hCryptoTwo.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null)); }
        double valueChange24hCryptoThree = dataCryptoValute.getData().get(2).getQuote().getUsdDataCoin().getPercentChange24h();
        TextView textViewChange24hCryptoThree = getActivity().findViewById(R.id.percent_change_24hCryptoThree);
        textViewChange24hCryptoThree.setText(String.format("%.2f%%", valueChange24hCryptoThree));
        if(valueChange24hCryptoThree>=0){ textViewChange24hCryptoThree.setTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null)); }
        else {textViewChange24hCryptoThree.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null)); }
    }
}
