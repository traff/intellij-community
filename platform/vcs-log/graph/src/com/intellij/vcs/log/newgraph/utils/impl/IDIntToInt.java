/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.vcs.log.newgraph.utils.impl;

import com.intellij.vcs.log.newgraph.utils.IntToIntMap;

public class IDIntToInt implements IntToIntMap {
  private final int size;

  public IDIntToInt(int size) {
    this.size = size;
  }

  @Override
  public int shortSize() {
    return size;
  }

  @Override
  public int longSize() {
    return size;
  }

  @Override
  public int getLongIndex(int shortIndex) {
    return shortIndex;
  }

  @Override
  public int getShortIndex(int longIndex) {
    return longIndex;
  }
}
