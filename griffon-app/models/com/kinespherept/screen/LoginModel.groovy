package com.kinespherept.screen

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonModel)
@Slf4j
class LoginModel {
    @FXObservable String username
    @FXObservable String password
    @FXObservable String message

    boolean unableToConnectToDb = false
}