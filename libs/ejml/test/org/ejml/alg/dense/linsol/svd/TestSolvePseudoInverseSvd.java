/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml.alg.dense.linsol.svd;

import org.ejml.alg.dense.linsol.GenericLinearSolverChecks;
import org.ejml.alg.dense.linsol.GenericSolvePseudoInverseChecks;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolver;
import org.junit.Test;

/**
 * @author Peter Abeles
 */
public class TestSolvePseudoInverseSvd extends GenericLinearSolverChecks {

    public TestSolvePseudoInverseSvd() {
        this.shouldFailSingular = false;
    }

    @Override
    protected LinearSolver<DenseMatrix64F> createSolver( DenseMatrix64F A ) {
        return new SolvePseudoInverseSvd(A.numRows,A.numCols);
    }

    @Test
    public void checkSingularBasic() {
        LinearSolver<DenseMatrix64F> solver = new SolvePseudoInverseSvd(10,10);
        GenericSolvePseudoInverseChecks checks = new GenericSolvePseudoInverseChecks(solver);

        checks.all();
    }
}
