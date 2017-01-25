package tictactoe.controllers.webControllers

import com.google.inject.{Inject, Provider}


/**
  */
class WebControllerContainer @Inject private(
                                              val auth: Auth,
                                              val userController: UserController,
                                              val gameController: GameController,
                                              val ticTacToeApplication: TicTacToeApplication,
                                              val socialAuthController: SocialAuthController
                                            ) {
}


trait WebController {

  val webProvider: Provider[WebControllerContainer]

  def Auth: Auth = webProvider.get().auth

  def GameController: GameController = webProvider.get().gameController

  def UserController: UserController = webProvider.get().userController

  def TicTacToeApplication: TicTacToeApplication = webProvider.get().ticTacToeApplication

  def SocialAuthController: SocialAuthController = webProvider.get().socialAuthController
}
