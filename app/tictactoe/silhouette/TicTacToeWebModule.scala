package tictactoe.silhouette

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import com.mohiva.play.silhouette.api.crypto.{CookieSigner, Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api.util.{FingerprintGenerator, IDGenerator, PasswordHasher, _}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCookieSigner, JcaCookieSignerSettings, JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{CookieStateProvider, CookieStateSettings}
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GitHubProvider, GoogleProvider}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.{DelegableAuthInfoDAO, InMemoryAuthInfoDAO}
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import tictactoe.mailer.{MailService, MailServiceImpl, MailTokenUser}
import tictactoe.model.User

import scala.concurrent.ExecutionContext.Implicits.{global => executionContext}


/** Configuration for dependency injection with Guice
  *
  */
class TicTacToeWebModule extends AbstractModule with ScalaModule {

  def configure(): Unit = {
    bind[Silhouette[TicTacToeEnv]].to[SilhouetteProvider[TicTacToeEnv]]
    bind[SecuredErrorHandler].to[TicTacToeSecuredErrorHandler]
    bind[UnsecuredErrorHandler].to[TicTacToeUnsecuredErrorHandler]
    bind[IdentityService[User]].to[UserService]
    bind[MailService].to[MailServiceImpl]
    bind[MailTokenService[MailTokenUser]].to[MailTokenUserService]

    bind[IDGenerator].toInstance(new SecureRandomIDGenerator()(executionContext))
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    //FIXME Update PasswordInfoDAO
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO] //.in[Singleton]
    //    bind[DelegableAuthInfoDAO[PasswordInfo]].toInstance(new InMemoryAuthInfoDAO[PasswordInfo])
    bind[DelegableAuthInfoDAO[OAuth1Info]].toInstance(new InMemoryAuthInfoDAO[OAuth1Info])
    bind[DelegableAuthInfoDAO[OAuth2Info]].toInstance(new InMemoryAuthInfoDAO[OAuth2Info])
    bind[DelegableAuthInfoDAO[OpenIDInfo]].toInstance(new InMemoryAuthInfoDAO[OpenIDInfo])
  }

  /**
    * Provides the Silhouette environment.
    *
    * @param userService          The user service implementation.
    * @param authenticatorService The tictactoe.authentication service implementation.
    * @param eventBus             The event bus instance.
    * @return The Silhouette environment.
    */
  @Provides
  def provideEnvironment(
                          userService: UserService,
                          authenticatorService: AuthenticatorService[CookieAuthenticator],
                          eventBus: EventBus
                        ): Environment[TicTacToeEnv] = {
    Environment[TicTacToeEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  private def ficusConfig(configuration: Configuration): FicusConfig = {
    Ficus.toFicusConfig(configuration.underlying)
  }


  /**
    * Provides the social provider registry.
    *
    * @param facebookProvider The Facebook provider implementation.
    * @param googleProvider   The Google provider implementation.
    * @param gitHubProvider   The GitHub provider implementation.
    * @return The Silhouette environment.
    */
  @Provides
  def provideSocialProviderRegistry(
                                     facebookProvider: FacebookProvider,
                                     googleProvider: GoogleProvider,
                                     gitHubProvider: GitHubProvider): SocialProviderRegistry = {

    SocialProviderRegistry(Seq(
      googleProvider,
      facebookProvider,
      gitHubProvider
    ))
  }


  /**
    * Provides the cookie signer for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The cookie signer for the authenticator.
    */
  @Provides
  def provideAuthenticatorCookieSigner(configuration: Configuration): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.authenticator.cookie.signer")

    //    val config = ficusConfig(configuration).as[JcaCookieSignerSettings]("tictactoe.silhouette.authenticator.cookie.signer")
    new JcaCookieSigner(config)
  }

  /**
    * Provides the crypter for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The crypter for the authenticator.
    */
  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = ficusConfig(configuration).as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }


  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @param oauth1InfoDAO   The implementation of the delegable OAuth1 auth info DAO.
    * @param oauth2InfoDAO   The implementation of the delegable OAuth2 auth info DAO.
    * @param openIDInfoDAO   The implementation of the delegable OpenID auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  def provideAuthInfoRepository(
                                 passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
                                 oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
                                 oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info],
                                 openIDInfoDAO: DelegableAuthInfoDAO[OpenIDInfo]): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO, oauth1InfoDAO, oauth2InfoDAO, openIDInfoDAO)
  }

  /**
    * Provides the authenticator service.
    *
    * @param cookieSigner         The cookie signer implementation.
    * @param crypter              The crypter implementation.
    * @param fingerprintGenerator The fingerprint generator implementation.
    * @param idGenerator          The ID generator implementation.
    * @param configuration        The Play configuration.
    * @param clock                The clock instance.
    * @return The authenticator service.
    */
  @Provides
  def provideAuthenticatorService(
                                   cookieSigner: CookieSigner,
                                   crypter: Crypter,
                                   fingerprintGenerator: FingerprintGenerator,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock
                                 ): AuthenticatorService[CookieAuthenticator] = {
    val config = ficusConfig(configuration).as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)
    new CookieAuthenticatorService(config, None, cookieSigner, encoder, fingerprintGenerator, idGenerator, clock)
  }

  /**
    * Provides the password hasher registry.
    *
    * @param passwordHasher The default password hasher implementation.
    * @return The password hasher registry.
    */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }

  /**
    * Provides the credentials provider.
    *
    * @param authInfoRepository     The auth info repository implementation.
    * @param passwordHasherRegistry The password hasher registry.
    * @return The credentials provider.
    */
  @Provides
  def provideCredentialsProvider(
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasherRegistry: PasswordHasherRegistry
                                ): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  /**
    * Provides the Facebook provider.
    *
    * @param httpLayer     The HTTP layer implementation.
    * @param stateProvider The OAuth2 state provider implementation.
    * @param configuration The Play configuration.
    * @return The Facebook provider.
    */
  @Provides
  def provideFacebookProvider(
                               httpLayer: HTTPLayer,
                               stateProvider: OAuth2StateProvider,
                               configuration: Configuration): FacebookProvider = {

    new FacebookProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.facebook"))
  }

  /**
    * Provides the Google provider.
    *
    * @param httpLayer     The HTTP layer implementation.
    * @param stateProvider The OAuth2 state provider implementation.
    * @param configuration The Play configuration.
    * @return The Google provider.
    */
  @Provides
  def provideGoogleProvider(
                             httpLayer: HTTPLayer,
                             stateProvider: OAuth2StateProvider,
                             configuration: Configuration): GoogleProvider = {

    new GoogleProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.google"))
  }

  @Provides
  def provideGithubProvider(httpLayer: HTTPLayer,
                            stateProvider: OAuth2StateProvider,
                            configuration: Configuration): GitHubProvider = {

    new GitHubProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.github"))
  }

  /**
    * Provides the HTTP layer implementation.
    *
    * @param client Play's WS client.
    * @return The HTTP layer implementation.
    */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)


  /**
    * Provides the OAuth2 state provider.
    *
    * @param idGenerator   The ID generator implementation.
    * @param cookieSigner  The cookie signer implementation.
    * @param configuration The Play configuration.
    * @param clock         The clock instance.
    * @return The OAuth2 state provider implementation.
    */
  @Provides
  def provideOAuth2StateProvider(
                                  idGenerator: IDGenerator,
                                  @Named("oauth2-state-cookie-signer") cookieSigner: CookieSigner,
                                  configuration: Configuration,
                                  clock: Clock): OAuth2StateProvider = {

    val settings = configuration.underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, idGenerator, cookieSigner, clock)
  }

  /**
    * Provides the cookie signer for the OAuth2 state provider.
    *
    * @param configuration The Play configuration.
    * @return The cookie signer for the OAuth2 state provider.
    */
  @Provides
  @Named("oauth2-state-cookie-signer")
  def provideOAuth2StageCookieSigner(configuration: Configuration): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.oauth2StateProvider.cookie.signer")

    new JcaCookieSigner(config)
  }
}
