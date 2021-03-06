package com.renatasarmet.android.prova2renata.Actions;

import android.util.Log;

import com.google.gson.Gson;
import com.renatasarmet.android.prova2renata.Entity.ActionEntity;
import com.renatasarmet.android.prova2renata.Entity.ActionListEntity;
import com.renatasarmet.android.prova2renata.Network.api.SocialActionsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionsPresenter {

    private ActionsView actionsView;
    private ActionListEntity actionListEntity;
    private List<ActionEntity> actionsList;

    ActionsPresenter(ActionsView actionsView){
        this.actionsView = actionsView;
    }

    protected void updateList(String jsonActions) {

        //verifica se há informações no json
        if(jsonActions != null){
            actionListEntity = new Gson().fromJson(jsonActions, ActionListEntity.class);
            actionsList = actionListEntity.getActions();
            actionsView.updateList(actionsList);

            //se não houver informações previamente no json, é necessário baixá-las
        }else {

            final SocialActionsApi socialActionsApi = SocialActionsApi.getInstance();
            actionsView.showLoading();
            socialActionsApi.getActions().enqueue(new Callback<ActionListEntity>() {
                @Override
                public void onResponse(Call<ActionListEntity> call, Response<ActionListEntity> response) {
                    actionsView.hideLoading();
                    actionListEntity = response.body();

                    if (actionListEntity != null && actionListEntity.getActions() != null) {
                        actionsList = actionListEntity.getActions();
                        actionsView.updateList(actionsList);

                    } else {
                        actionsView.showMessage("Falha no acesso");
                    }
                }

                @Override
                public void onFailure(Call<ActionListEntity> call, Throwable t) {
                    actionsView.hideLoading();
                    actionsView.showMessage("Falha no acesso ao servidor");
                    actionsView.showMessage("Nenhuma informação salva");
                }
            });
        }
    }

    public void saveActions() {
        String jsonActionEntity = new Gson().toJson(actionListEntity);
        actionsView.saveActionsInSharedPreferences(jsonActionEntity);
    }

}
