package org.colibrifw.common.utils

import jp.t2v.lab.play20.auth.AuthConfig
import org.colibrifw.common.models
import play.api.mvc.PlainResult
import play.api.mvc.Request
import play.api.mvc.Results._
import org.colibrifw.controllers.routes
import play.api.mvc.Session

trait AuthConfigImpl extends AuthConfig {
  /**
   * ユーザを識別するIDの型です。String や Int や Long などが使われるでしょう。
   */
  type Id = Int

  /**
   * あなたのアプリケーションで認証するユーザを表す型です。
   * User型やAccount型など、アプリケーションに応じて設定してください。
   */
  type User = models.User

  /**
   * 認可(権限チェック)を行う際に、アクション毎に設定するオブジェクトの型です。
   * このサンプルでは例として以下のような trait を使用しています。
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = models.Authority

  /**
   * CacheからユーザIDを取り出すための ClassManifest です。
   * 基本的にはこの例と同じ記述をして下さい。
   */
  val idManifest: ClassManifest[Id] = classManifest[Id]

  /**
   * セッションタイムアウトの時間(秒)です。
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * ユーザIDからUserブジェクトを取得するアルゴリズムを指定します。
   * 任意の処理を記述してください。
   */
  def resolveUser(id: Id): Option[User] = models.User(id)

  /**
   * ログインが成功した際に遷移する先を指定します。
   */
  def loginSucceeded[A](request: Request[A]): PlainResult =
    Redirect(routes.Login.index)

  /**
   * ログアウトが成功した際に遷移する先を指定します。
   */
  def logoutSucceeded[A](request: Request[A]): PlainResult =
    Redirect(routes.Login.index).withSession(request.session - "loginUser")

  /**
   * 認証が失敗した場合に遷移する先を指定します。
   */
  def authenticationFailed[A](request: Request[A]): PlainResult =
    Redirect(routes.Login.index)

  /**
   * 認可(権限チェック)が失敗した場合に遷移する先を指定します。
   */
  def authorizationFailed[A](request: Request[A]): PlainResult =
    Forbidden("no permission")

  /**
   * 権限チェックのアルゴリズムを指定します。
   * 任意の処理を記述してください。
   */
  def authorize(user: User, authority: Authority): Boolean =
    user.organization_id == authority.organization_id
    /*) match {
      case (Administrator, _) => true
      case (NormalUser, NormalUser) => true
      case _ => false
    }*/
}