/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
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
package com.biopatternsg.infrastructure.adapters.in;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import com.biopatternsg.infrastructure.session.SessionUtil;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Dispatches pipeline execution asynchronously while propagating the caller's request context and session headers.
 */
@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AsyncPipelineDispatcher {

    private final LaunchPipeline launchPipeline;
    private final SessionUtil sessionUtil;

    public void dispatch(PipelineConfig pipelineConfig) {
        MultivaluedMap<String, String> contextToPropagate = copySessionContext();

        CompletableFuture.runAsync(() -> executeWithRequestContext(pipelineConfig, contextToPropagate));
    }

    private MultivaluedMap<String, String> copySessionContext() {
        if (sessionUtil.getContext() == null) {
            return null;
        }
        return new MultivaluedHashMap<>(sessionUtil.getContext());
    }

    private void executeWithRequestContext(PipelineConfig pipelineConfig, MultivaluedMap<String, String> contextToPropagate) {
        ManagedContext requestContext = Arc.container().requestContext();
        boolean newlyActivated = false;
        if (!requestContext.isActive()) {
            requestContext.activate();
            newlyActivated = true;
        }
        try {
            if (contextToPropagate != null) {
                sessionUtil.setContext(contextToPropagate);
            }
            launchPipeline.execute(pipelineConfig);
        } catch (Exception e) {
            log.error("Error launch pipeline", e);
        } finally {
            if (newlyActivated) {
                requestContext.terminate();
            }
        }
    }
}
