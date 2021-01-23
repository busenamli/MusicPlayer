package com.busenamli.musicplayerdeneme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        //Seçilen Fragment'a göre o fragment'ı başlatıyor
        Fragment selectedFragment;

        switch (position){
            case 0 :
                selectedFragment = SongsFragment.newInstance();
                break;
            case 1 :
                selectedFragment = AlbumsFragment.newInstance();
                break;
            default:
                return null;
        }
        return selectedFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        //Hangi pozisyonda hangi başlık yazacağını ayarlıyor
        CharSequence selectedTitle;
        switch (position){
            case 0 :
                selectedTitle = "Songs";
                break;
            case 1 :
                selectedTitle = "Albums";
                break;
            default:
                return null;

        }
        return selectedTitle;
    }

    @Override
    public int getCount() {
        return 2; //2 seçenek var
    }
}
