/*******************************************************************************
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH and others
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/
package org.eclipse.tractusx.semantics.registry.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.tractusx.semantics.registry.model.Shell;
import org.eclipse.tractusx.semantics.registry.model.ShellIdentifier;
import org.eclipse.tractusx.semantics.registry.model.projection.ShellIdentifierMinimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShellIdentifierRepository extends JpaRepository<ShellIdentifier, UUID> {

   @Modifying
   @Query( value = "DELETE FROM SHELL_IDENTIFIER WHERE fk_shell_id = :shellId AND namespace != :keyToIgnore", nativeQuery = true )
   void deleteShellIdentifiersByShellId( UUID shellId, String keyToIgnore );

   Set<ShellIdentifier> findByShellId( Shell shellId );

   @Query( value = """
            SELECT NEW org.eclipse.tractusx.semantics.registry.model.projection.ShellIdentifierMinimal(sid.shellId.idExternal, sid.key, sid.value)
            FROM ShellIdentifier sid
            WHERE
                 sid.shellId.id IN (
                    SELECT filtersid.shellId.id
                    FROM ShellIdentifier filtersid
                    WHERE
                        CONCAT(filtersid.key, filtersid.value) IN (:keyValueCombinations)
                    GROUP BY filtersid.shellId.id
                    HAVING COUNT(*) = :keyValueCombinationsSize
                )
         """)
   List<ShellIdentifierMinimal> findMinimalShellIdsBySpecificAssetIds( List<String> keyValueCombinations, int keyValueCombinationsSize );
}
