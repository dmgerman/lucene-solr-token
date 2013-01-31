begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Simple utility functions for the faceting examples  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ExampleUtils
specifier|public
class|class
name|ExampleUtils
block|{
comment|/** No instance */
DECL|method|ExampleUtils
specifier|private
name|ExampleUtils
parameter_list|()
block|{}
comment|/**     * True if the system property<code>tests.verbose</code> has been set.    * If true, it causes {@link #log(Object)} to print messages to the console.    */
DECL|field|VERBOSE
specifier|public
specifier|static
specifier|final
name|boolean
name|VERBOSE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tests.verbose"
argument_list|)
decl_stmt|;
comment|/** The Lucene {@link Version} used by the example code. */
DECL|field|EXAMPLE_VER
specifier|public
specifier|static
specifier|final
name|Version
name|EXAMPLE_VER
init|=
name|Version
operator|.
name|LUCENE_40
decl_stmt|;
comment|/**    * Logs the String representation of<code>msg</code> to the console,    * if {@link #VERBOSE} is true. Otherwise, does nothing.    * @see #VERBOSE    */
DECL|method|log
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|Object
name|msg
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

