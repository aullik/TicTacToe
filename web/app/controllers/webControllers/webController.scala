package controllers.webControllers

import com.google.inject.{Inject, Provider}

/**
  */
class WebControllerContainer @Inject private(
                                              val auth: Auth,
                                              val userController: UserController
                                            ) {
  val gameCont = GameController
}


trait WebController {

  val webProvider: Provider[WebControllerContainer]

  def Auth = webProvider.get().auth

  def GameController = webProvider.get().gameCont

  def UserController = webProvider.get().userController

}
