begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSet
import|;
end_import

begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|CartesianShapeFilter
specifier|public
class|class
name|CartesianShapeFilter
extends|extends
name|Filter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|CartesianShapeFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    *     */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|CartesianShapeFilter
name|CartesianShapeFilter
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|area
init|=
name|shape
operator|.
name|getArea
argument_list|()
decl_stmt|;
name|int
name|sz
init|=
name|area
operator|.
name|size
argument_list|()
decl_stmt|;
name|log
operator|.
name|fine
argument_list|(
literal|"Area size "
operator|+
name|sz
argument_list|)
expr_stmt|;
comment|// iterate through each boxid
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|double
name|boxId
init|=
name|area
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|NumericUtils
operator|.
name|doubleToPrefixCoded
argument_list|(
name|boxId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// iterate through all documents
comment|// which have this boxId
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|bits
operator|.
name|fastSet
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"BoundaryBox Time Taken: "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" found: "
operator|+
name|bits
operator|.
name|cardinality
argument_list|()
operator|+
literal|" candidates"
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
block|}
end_class

end_unit

