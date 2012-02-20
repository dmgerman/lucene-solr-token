begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package

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
name|base
operator|.
name|context
operator|.
name|SpatialContext
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
name|base
operator|.
name|io
operator|.
name|LineReader
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
name|base
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
name|base
operator|.
name|query
operator|.
name|SpatialArgsParser
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

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
name|Iterator
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
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Helper class to execute queries  */
end_comment

begin_class
DECL|class|SpatialTestQuery
specifier|public
class|class
name|SpatialTestQuery
block|{
DECL|field|testname
specifier|public
name|String
name|testname
decl_stmt|;
DECL|field|line
specifier|public
name|String
name|line
decl_stmt|;
DECL|field|lineNumber
specifier|public
name|int
name|lineNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|args
specifier|public
name|SpatialArgs
name|args
decl_stmt|;
DECL|field|ids
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Get Test Queries    */
DECL|method|getTestQueries
specifier|public
specifier|static
name|Iterator
argument_list|<
name|SpatialTestQuery
argument_list|>
name|getTestQueries
parameter_list|(
specifier|final
name|SpatialArgsParser
name|parser
parameter_list|,
specifier|final
name|SpatialContext
name|ctx
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LineReader
argument_list|<
name|SpatialTestQuery
argument_list|>
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SpatialTestQuery
name|parseLine
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|SpatialTestQuery
name|test
init|=
operator|new
name|SpatialTestQuery
argument_list|()
decl_stmt|;
name|test
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|test
operator|.
name|lineNumber
operator|=
name|getLineNumber
argument_list|()
expr_stmt|;
try|try
block|{
comment|// skip a comment
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
name|int
name|idx
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|']'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|idx
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|test
operator|.
name|ids
operator|.
name|add
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|test
operator|.
name|args
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
return|return
name|test
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid query line: "
operator|+
name|test
operator|.
name|line
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|line
return|;
block|}
block|}
end_class

end_unit

