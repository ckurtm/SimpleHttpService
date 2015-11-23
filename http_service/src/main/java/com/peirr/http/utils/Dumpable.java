/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirr.http.utils;

import java.io.IOException;

public interface Dumpable {
    String dump();

    void dump(Appendable out, String indent) throws IOException;
}
