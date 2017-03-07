/*
 * Copyright 2016 Crown Copyright
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

package uk.gov.gchq.gaffer.operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.serialisation.TypeReferenceImpl;

public abstract class AbstractGetIterableElementsOperation<SEED_TYPE, RESULT_TYPE>
        extends AbstractGetIterableOperation<SEED_TYPE, RESULT_TYPE> implements GetIterableElementsOperation<SEED_TYPE, RESULT_TYPE> {
    private DirectedType directedType = DirectedType.BOTH;
    private IncludeIncomingOutgoingType includeIncomingOutGoing = IncludeIncomingOutgoingType.BOTH;

    protected AbstractGetIterableElementsOperation() {
        super();
    }

    protected AbstractGetIterableElementsOperation(final Iterable<SEED_TYPE> seeds) {
        this(new WrappedCloseableIterable<>(seeds));
    }

    protected AbstractGetIterableElementsOperation(final CloseableIterable<SEED_TYPE> seeds) {
        super(seeds);
    }

    protected AbstractGetIterableElementsOperation(final View view) {
        super(view);
    }

    protected AbstractGetIterableElementsOperation(final View view, final Iterable<SEED_TYPE> seeds) {
        this(view, new WrappedCloseableIterable<>(seeds));
    }

    protected AbstractGetIterableElementsOperation(final View view, final CloseableIterable<SEED_TYPE> seeds) {
        super(view, seeds);
    }

    protected AbstractGetIterableElementsOperation(final GetIterableOperation<SEED_TYPE, ?> operation) {
        super(operation);
    }

    protected AbstractGetIterableElementsOperation(final GetIterableElementsOperation<SEED_TYPE, ?> operation) {
        super(operation);
        setDirectedType(operation.getDirectedType());
        setIncludeIncomingOutGoing(operation.getIncludeIncomingOutGoing());
    }

    @JsonIgnore
    @Override
    public CloseableIterable<SEED_TYPE> getInput() {
        return super.getInput();
    }

    @JsonProperty
    @Override
    public void setInput(final CloseableIterable<SEED_TYPE> input) {
        super.setInput(input);
    }

    @Override
    public IncludeIncomingOutgoingType getIncludeIncomingOutGoing() {
        return includeIncomingOutGoing;
    }

    @Override
    public void setIncludeIncomingOutGoing(final IncludeIncomingOutgoingType includeIncomingOutGoing) {
        this.includeIncomingOutGoing = includeIncomingOutGoing;
    }

    @Override
    public void setDirectedType(final DirectedType directedType) {
        this.directedType = directedType;
    }

    @Override
    public DirectedType getDirectedType() {
        return directedType;
    }

    @Override
    protected TypeReference createOutputTypeReference() {
        return new TypeReferenceImpl.CloseableIterableElement();
    }

    @Override
    public boolean validate(final Edge edge) {
        return null != edge && validateFlags(edge) && super.validate(edge);
    }

    public boolean validateFlags(final Edge edge) {
        return DirectedType.BOTH == getDirectedType()
                || (DirectedType.DIRECTED == getDirectedType() && edge.isDirected())
                || (DirectedType.UNDIRECTED == getDirectedType() && !edge.isDirected());
    }

    public abstract static class BaseBuilder<
            OP_TYPE extends AbstractGetIterableElementsOperation<SEED_TYPE, RESULT_TYPE>,
            SEED_TYPE,
            RESULT_TYPE,
            CHILD_CLASS extends BaseBuilder<OP_TYPE, SEED_TYPE, RESULT_TYPE, ?>
            >
            extends AbstractGetIterableOperation.BaseBuilder<OP_TYPE, SEED_TYPE, RESULT_TYPE, CHILD_CLASS> {

        protected BaseBuilder(final OP_TYPE op) {
            super(op);
        }

        /**
         * Builds the operation and returns it.
         *
         * @return the built operation.
         */
        public OP_TYPE build() {
            if (null == op.getSeeds()) {
                if (seeds != null) {
                    op.setSeeds(seeds);
                }
            }
            return op;
        }

        /**
         * @param inOutType sets the includeIncomingOutGoing option on the operation.
         * @return this Builder
         * @see GetElementsOperation#setIncludeIncomingOutGoing(IncludeIncomingOutgoingType)
         */
        public CHILD_CLASS inOutType(final IncludeIncomingOutgoingType inOutType) {
            op.setIncludeIncomingOutGoing(inOutType);
            return self();
        }

        /**
         * @param directedType sets the directedType option on the operation.
         * @return this Builder
         */
        public CHILD_CLASS directedType(final DirectedType directedType) {
            op.setDirectedType(directedType);
            return self();
        }
    }

    public static final class Builder<OP_TYPE extends AbstractGetIterableElementsOperation<SEED_TYPE, RESULT_TYPE>, SEED_TYPE, RESULT_TYPE>
            extends BaseBuilder<OP_TYPE, SEED_TYPE, RESULT_TYPE, Builder<OP_TYPE, SEED_TYPE, RESULT_TYPE>> {

        protected Builder(final OP_TYPE op) {
            super(op);
        }

        @Override
        protected Builder<OP_TYPE, SEED_TYPE, RESULT_TYPE> self() {
            return this;
        }
    }
}
