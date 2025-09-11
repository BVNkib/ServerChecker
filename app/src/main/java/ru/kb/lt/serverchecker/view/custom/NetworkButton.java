package ru.kb.lt.serverchecker.view.custom;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ru.kb.lt.serverchecker.R;
import ru.kb.lt.serverchecker.databinding.ButtonNetworkBinding;

public class NetworkButton {
    private final Context context;
    private final ButtonNetworkBinding binding;

    public NetworkButton(ButtonNetworkBinding binding, Context context) {
        this.binding = binding;
        this.context = context;
    }

    public void setProgress() {
        setColor(R.color.links);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.line);
        Animation anim1 = AnimationUtils.loadAnimation(context, R.anim.line1);
        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.line2);
        Animation anim3 = AnimationUtils.loadAnimation(context, R.anim.line3);

        binding.line.startAnimation(anim);
        binding.line1.startAnimation(anim1);
        binding.line2.startAnimation(anim2);
        binding.line3.startAnimation(anim3);
    }

    public void stopProgress() {
        binding.line.clearAnimation();
        binding.line1.clearAnimation();
        binding.line2.clearAnimation();
        binding.line3.clearAnimation();
    }

    public void setGood() {
        this.stopProgress();
        setColor(R.color.good);
    }

    public void setBad() {
        this.stopProgress();
        setColor(R.color.bad);
    }

    private void setColor(int color) {
        binding.line.setBackgroundColor(context.getColor(color));
        binding.line1.setBackgroundColor(context.getColor(color));
        binding.line2.setBackgroundColor(context.getColor(color));
        binding.line3.setBackgroundColor(context.getColor(color));
    }
}
