package com.kinespherept.component

import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class NavigationModel {

    @MVCMember @Nonnull NavigationController controller

}
