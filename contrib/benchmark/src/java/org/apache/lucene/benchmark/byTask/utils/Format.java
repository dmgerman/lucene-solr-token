begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
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
name|utils
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_comment
comment|/**  * Formatting utilities (for reports).  */
end_comment

begin_class
DECL|class|Format
specifier|public
class|class
name|Format
block|{
DECL|field|numFormat
specifier|private
specifier|static
name|NumberFormat
name|numFormat
index|[]
init|=
block|{
name|NumberFormat
operator|.
name|getInstance
argument_list|()
block|,
name|NumberFormat
operator|.
name|getInstance
argument_list|()
block|,
name|NumberFormat
operator|.
name|getInstance
argument_list|()
block|,   }
decl_stmt|;
DECL|field|padd
specifier|private
specifier|static
specifier|final
name|String
name|padd
init|=
literal|"                                                 "
decl_stmt|;
static|static
block|{
name|numFormat
index|[
literal|0
index|]
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numFormat
index|[
literal|0
index|]
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numFormat
index|[
literal|1
index|]
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|numFormat
index|[
literal|1
index|]
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|numFormat
index|[
literal|2
index|]
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|numFormat
index|[
literal|2
index|]
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Padd a number from left.    * @param numFracDigits number of digits in fraction part - must be 0 or 1 or 2.    * @param f number to be formatted.    * @param col column name (used for deciding on length).    * @return formatted string.    */
DECL|method|format
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|int
name|numFracDigits
parameter_list|,
name|float
name|f
parameter_list|,
name|String
name|col
parameter_list|)
block|{
name|String
name|res
init|=
name|padd
operator|+
name|numFormat
index|[
name|numFracDigits
index|]
operator|.
name|format
argument_list|(
name|f
argument_list|)
decl_stmt|;
return|return
name|res
operator|.
name|substring
argument_list|(
name|res
operator|.
name|length
argument_list|()
operator|-
name|col
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Padd a number from right.    * @param numFracDigits number of digits in fraction part - must be 0 or 1 or 2.    * @param f number to be formatted.    * @param col column name (used for deciding on length).    * @return formatted string.    */
DECL|method|formatPaddRight
specifier|public
specifier|static
name|String
name|formatPaddRight
parameter_list|(
name|int
name|numFracDigits
parameter_list|,
name|float
name|f
parameter_list|,
name|String
name|col
parameter_list|)
block|{
name|String
name|res
init|=
name|numFormat
index|[
name|numFracDigits
index|]
operator|.
name|format
argument_list|(
name|f
argument_list|)
operator|+
name|padd
decl_stmt|;
return|return
name|res
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|col
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Padd a number from left.    * @param n number to be formatted.    * @param col column name (used for deciding on length).    * @return formatted string.    */
DECL|method|format
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|col
parameter_list|)
block|{
name|String
name|res
init|=
name|padd
operator|+
name|n
decl_stmt|;
return|return
name|res
operator|.
name|substring
argument_list|(
name|res
operator|.
name|length
argument_list|()
operator|-
name|col
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Padd a string from right.    * @param s string to be formatted.    * @param col column name (used for deciding on length).    * @return formatted string.    */
DECL|method|format
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|col
parameter_list|)
block|{
name|String
name|s1
init|=
operator|(
name|s
operator|+
name|padd
operator|)
decl_stmt|;
return|return
name|s1
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|col
operator|.
name|length
argument_list|()
argument_list|,
name|s1
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Padd a string from left.    * @param s string to be formatted.    * @param col column name (used for deciding on length).    * @return formatted string.    */
DECL|method|formatPaddLeft
specifier|public
specifier|static
name|String
name|formatPaddLeft
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|col
parameter_list|)
block|{
name|String
name|res
init|=
name|padd
operator|+
name|s
decl_stmt|;
return|return
name|res
operator|.
name|substring
argument_list|(
name|res
operator|.
name|length
argument_list|()
operator|-
name|col
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Extract simple class name     * @param cls class whose simple name is required     * @return simple class name    */
DECL|method|simpleName
specifier|public
specifier|static
name|String
name|simpleName
parameter_list|(
name|Class
name|cls
parameter_list|)
block|{
name|String
name|c
init|=
name|cls
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|p
init|=
name|cls
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|k
init|=
name|c
operator|.
name|lastIndexOf
argument_list|(
name|p
operator|+
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|c
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
operator|+
name|p
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

