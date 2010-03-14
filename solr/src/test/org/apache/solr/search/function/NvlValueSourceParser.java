begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|queryParser
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|FunctionQParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|ValueSourceParser
import|;
end_import

begin_comment
comment|/**  * A sample ValueSourceParser for testing. Approximates the oracle NVL function,  * letting you substitude a value when a "null" is encountered. In this case,  * null is approximated by a float value, since ValueSource always returns a  * float, even if the field is undefined for a document.  *   * Initialization parameters:  *  - nvlFloatValue: float value to consider as "NULL" when seen in a field. defaults to 0.0f.  *    * Example:  *   nvl(vs,2)   will return 2 if the vs is NULL (as defined by nvlFloatValue above) or the doc value otherwise  *   */
end_comment

begin_class
DECL|class|NvlValueSourceParser
specifier|public
class|class
name|NvlValueSourceParser
extends|extends
name|ValueSourceParser
block|{
comment|/**      * Value to consider "null" when found in a ValueSource Defaults to 0.0      */
DECL|field|nvlFloatValue
specifier|private
name|float
name|nvlFloatValue
init|=
literal|0.0f
decl_stmt|;
DECL|method|parse
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
specifier|final
name|float
name|nvl
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleFloatFunction
argument_list|(
name|source
argument_list|)
block|{
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"nvl"
return|;
block|}
specifier|protected
name|float
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|vals
parameter_list|)
block|{
name|float
name|v
init|=
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
name|nvlFloatValue
condition|)
block|{
return|return
name|nvl
return|;
block|}
else|else
block|{
return|return
name|v
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|/* initialize the value to consider as null */
name|Float
name|nvlFloatValueArg
init|=
operator|(
name|Float
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"nvlFloatValue"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nvlFloatValueArg
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nvlFloatValue
operator|=
name|nvlFloatValueArg
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

