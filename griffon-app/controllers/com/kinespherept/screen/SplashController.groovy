package com.kinespherept.screen

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonController)
@Slf4j
class SplashController {

    @MVCMember @Nonnull SplashModel model
    @MVCMember @Nonnull SplashView view

    @GriffonAutowire LoginController loginController

    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
        log.debug "SplashController.init() :: view=${view}"
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void go() {
        // prepare the login form and change to it
        loginController.prepareForm()
        SceneManager.getManager().changeScene(SceneDefinition.LOGIN)
    }
}