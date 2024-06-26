/*
 * Copyright 2021-2023 Creek Contributors (https://github.com/creek-service)
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

package org.creekservice.internal.example2;

import org.creekservice.api.example2.Example;

/** Example impl */
public final class ExampleImpl implements Example {

    /**
     * Example method
     *
     * @return {@code true}
     */
    @Override
    public boolean getTrue() {
        return true;
    }
}
