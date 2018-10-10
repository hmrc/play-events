/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.play.events.{Measurable, AlertCode}
import scala.concurrent.duration.Duration

case class DefaultTimerEvent(source: String,
                             name: String,
                             data: Map[String, String]) extends Measurable

object DefaultTimerEvent {

  def apply(source: String, alertCode: AlertCode, duration: Duration) = new DefaultTimerEvent(
    source = source,
    name = s"Timer-$alertCode",
    data = Map (
      "Duration" -> s"${duration.length}",
      "Unit" -> s"${duration.unit.name}"
    )
  )
}
