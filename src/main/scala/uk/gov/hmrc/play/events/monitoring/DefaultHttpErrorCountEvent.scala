/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.events.monitoring

import uk.gov.hmrc.play.events._
import uk.gov.hmrc.http.{HttpException, Upstream5xxResponse, Upstream4xxResponse}

case class DefaultHttpErrorCountEvent(source: String,
                                      name: String,
                                      data: Map[String, String]) extends Measurable

object DefaultHttpErrorCountEvent {

  def apply(source: String, response: Upstream4xxResponse, alertCode: AlertCode) = new DefaultHttpErrorCountEvent(
    source = source,
    name = "Http4xxErrorCount",
    data = Map (
      "Count" -> "1"
    )
  )

  def apply(source: String, response: Upstream5xxResponse, alertCode: AlertCode) = new DefaultHttpErrorCountEvent(
    source = source,
    name = "Http5xxErrorCount",
    data = Map (
      "Count" -> "1"
    )
  )

  def apply(source: String, exception: HttpException, alertCode: AlertCode) = new DefaultHttpErrorCountEvent(
    source = source,
    if (exception.responseCode >= 500) "Http5xxErrorCount" else "Http4xxErrorCount",
    data = Map (
      "Count" -> "1"
    )
  )
}
