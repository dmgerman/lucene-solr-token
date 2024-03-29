begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|spatial
operator|.
name|SpatialStrategy
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
import|;
end_import

begin_comment
comment|/**  * Reads spatial data from the body field docs from an internally created {@link LineDocSource}.  * It's parsed by {@link org.locationtech.spatial4j.context.SpatialContext#readShapeFromWkt(String)} (String)} and then  * further manipulated via a configurable {@link SpatialDocMaker.ShapeConverter}. When using point  * data, it's likely you'll want to configure the shape converter so that the query shapes actually  * cover a region. The queries are all created and cached in advance. This query maker works in  * conjunction with {@link SpatialDocMaker}.  See spatial.alg for a listing of options, in  * particular the options starting with "query.".  */
end_comment

begin_class
DECL|class|SpatialFileQueryMaker
specifier|public
class|class
name|SpatialFileQueryMaker
extends|extends
name|AbstractQueryMaker
block|{
DECL|field|strategy
specifier|protected
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|field|distErrPct
specifier|protected
name|double
name|distErrPct
decl_stmt|;
comment|//NaN if not set
DECL|field|operation
specifier|protected
name|SpatialOperation
name|operation
decl_stmt|;
DECL|field|score
specifier|protected
name|boolean
name|score
decl_stmt|;
DECL|field|shapeConverter
specifier|protected
name|SpatialDocMaker
operator|.
name|ShapeConverter
name|shapeConverter
decl_stmt|;
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|strategy
operator|=
name|SpatialDocMaker
operator|.
name|getSpatialStrategy
argument_list|(
name|config
operator|.
name|getRoundNumber
argument_list|()
argument_list|)
expr_stmt|;
name|shapeConverter
operator|=
name|SpatialDocMaker
operator|.
name|makeShapeConverter
argument_list|(
name|strategy
argument_list|,
name|config
argument_list|,
literal|"query.spatial."
argument_list|)
expr_stmt|;
name|distErrPct
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"query.spatial.distErrPct"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|operation
operator|=
name|SpatialOperation
operator|.
name|get
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"query.spatial.predicate"
argument_list|,
literal|"Intersects"
argument_list|)
argument_list|)
expr_stmt|;
name|score
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"query.spatial.score"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|//call last, will call prepareQueries()
block|}
annotation|@
name|Override
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxQueries
init|=
name|config
operator|.
name|get
argument_list|(
literal|"query.file.maxQueries"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|Config
name|srcConfig
init|=
operator|new
name|Config
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|srcConfig
operator|.
name|set
argument_list|(
literal|"docs.file"
argument_list|,
name|config
operator|.
name|get
argument_list|(
literal|"query.file"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|srcConfig
operator|.
name|set
argument_list|(
literal|"line.parser"
argument_list|,
name|config
operator|.
name|get
argument_list|(
literal|"query.file.line.parser"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|srcConfig
operator|.
name|set
argument_list|(
literal|"content.source.forever"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|LineDocSource
name|src
init|=
operator|new
name|LineDocSource
argument_list|()
decl_stmt|;
try|try
block|{
name|src
operator|.
name|setConfig
argument_list|(
name|srcConfig
argument_list|)
expr_stmt|;
name|src
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|DocData
name|docData
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxQueries
condition|;
name|i
operator|++
control|)
block|{
name|docData
operator|=
name|src
operator|.
name|getNextDocData
argument_list|(
name|docData
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
name|SpatialDocMaker
operator|.
name|makeShapeFromString
argument_list|(
name|strategy
argument_list|,
name|docData
operator|.
name|getName
argument_list|()
argument_list|,
name|docData
operator|.
name|getBody
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|shape
operator|=
name|shapeConverter
operator|.
name|convert
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|makeQueryFromShape
argument_list|(
name|shape
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|i
operator|--
expr_stmt|;
comment|//skip
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
comment|//all-done
block|}
finally|finally
block|{
name|src
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|queries
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
name|queries
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|makeQueryFromShape
specifier|protected
name|Query
name|makeQueryFromShape
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|operation
argument_list|,
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|distErrPct
argument_list|)
condition|)
name|args
operator|.
name|setDistErrPct
argument_list|(
name|distErrPct
argument_list|)
expr_stmt|;
name|Query
name|filterQuery
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|score
condition|)
block|{
comment|//wrap with distance computing query
name|ValueSource
name|valueSource
init|=
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|shape
operator|.
name|getCenter
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
comment|//matches everything and provides score
operator|.
name|add
argument_list|(
name|filterQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
comment|//filters (score isn't used)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|filterQuery
return|;
comment|// assume constant scoring
block|}
block|}
block|}
end_class

end_unit

