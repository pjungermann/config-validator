/*
 * Copyright 2015-2016 Patrick Jungermann
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
package configs;

env {
    aware = "default"
    override = "first"
}

environments {
    environment1 {
        env {
            aware = "by-environment1"
            override = "by-environment1"
        }
    }

    environment2 {
        env {
            aware = "by-environment1"
            override = "by-environment1"
        }
    }
}

env {
    override = "second"
}

list = [1,2,3]
closure = {
    method "call"
    property = "assignment"
}

int_value = Integer.MAX_VALUE
float_value = Float.MAX_VALUE
double_value = Double.MAX_VALUE
