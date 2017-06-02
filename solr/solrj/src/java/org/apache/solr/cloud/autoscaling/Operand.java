begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.autoscaling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|cloud
operator|.
name|autoscaling
operator|.
name|Clause
operator|.
name|TestStatus
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
operator|.
name|Clause
operator|.
name|TestStatus
operator|.
name|FAIL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
operator|.
name|Clause
operator|.
name|TestStatus
operator|.
name|NOT_APPLICABLE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
operator|.
name|Clause
operator|.
name|TestStatus
operator|.
name|PASS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
operator|.
name|Policy
operator|.
name|ANY
import|;
end_import

begin_enum
DECL|enum|Operand
specifier|public
enum|enum
name|Operand
block|{
DECL|method|WILDCARD
DECL|method|WILDCARD
name|WILDCARD
parameter_list|(
name|ANY
parameter_list|,
name|Integer
operator|.
name|MAX_VALUE
parameter_list|)
block|{
annotation|@
name|Override
specifier|public
name|TestStatus
name|match
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|testVal
operator|==
literal|null
condition|?
name|NOT_APPLICABLE
else|:
name|PASS
return|;
block|}
block|}
block|,
DECL|enum constant|EQUAL
name|EQUAL
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|_delta
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
return|return
name|expected
operator|-
name|actual
return|;
block|}
block|}
block|,
DECL|enum constant|NOT_EQUAL
name|NOT_EQUAL
argument_list|(
literal|"!"
argument_list|,
literal|2
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TestStatus
name|match
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|super
operator|.
name|match
argument_list|(
name|ruleVal
argument_list|,
name|testVal
argument_list|)
operator|==
name|PASS
condition|?
name|FAIL
else|:
name|PASS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|_delta
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
return|return
name|expected
operator|-
name|actual
return|;
block|}
block|}
block|,
DECL|enum constant|GREATER_THAN
name|GREATER_THAN
argument_list|(
literal|">"
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TestStatus
name|match
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
if|if
condition|(
name|testVal
operator|==
literal|null
condition|)
return|return
name|NOT_APPLICABLE
return|;
if|if
condition|(
name|ruleVal
operator|instanceof
name|Double
condition|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|Clause
operator|.
name|parseDouble
argument_list|(
literal|""
argument_list|,
name|testVal
argument_list|)
argument_list|,
operator|(
name|Double
operator|)
name|ruleVal
argument_list|)
operator|==
literal|1
condition|?
name|PASS
else|:
name|FAIL
return|;
block|}
return|return
name|getLong
argument_list|(
name|testVal
argument_list|)
operator|>
name|getLong
argument_list|(
name|ruleVal
argument_list|)
condition|?
name|PASS
else|:
name|FAIL
return|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|_delta
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
return|return
name|actual
operator|>
name|expected
condition|?
literal|0
else|:
operator|(
name|expected
operator|+
literal|1
operator|)
operator|-
name|actual
return|;
block|}
block|}
block|,
DECL|enum constant|LESS_THAN
name|LESS_THAN
argument_list|(
literal|"<"
argument_list|,
literal|2
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TestStatus
name|match
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
if|if
condition|(
name|testVal
operator|==
literal|null
condition|)
return|return
name|NOT_APPLICABLE
return|;
if|if
condition|(
name|ruleVal
operator|instanceof
name|Double
condition|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|Clause
operator|.
name|parseDouble
argument_list|(
literal|""
argument_list|,
name|testVal
argument_list|)
argument_list|,
operator|(
name|Double
operator|)
name|ruleVal
argument_list|)
operator|==
operator|-
literal|1
condition|?
name|PASS
else|:
name|FAIL
return|;
block|}
return|return
name|getLong
argument_list|(
name|testVal
argument_list|)
operator|<
name|getLong
argument_list|(
name|ruleVal
argument_list|)
condition|?
name|PASS
else|:
name|FAIL
return|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|_delta
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
return|return
name|actual
operator|<
name|expected
condition|?
literal|0
else|:
operator|(
name|expected
operator|)
operator|-
name|actual
return|;
block|}
block|}
block|;
DECL|field|operand
specifier|public
specifier|final
name|String
name|operand
decl_stmt|;
DECL|field|priority
specifier|final
name|int
name|priority
decl_stmt|;
DECL|method|Operand
name|Operand
parameter_list|(
name|String
name|val
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|operand
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
DECL|method|toStr
specifier|public
name|String
name|toStr
parameter_list|(
name|Object
name|expectedVal
parameter_list|)
block|{
return|return
name|operand
operator|+
name|expectedVal
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|match
specifier|public
name|TestStatus
name|match
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|ruleVal
argument_list|,
name|testVal
argument_list|)
condition|?
name|PASS
else|:
name|FAIL
return|;
block|}
DECL|method|getLong
name|Long
name|getLong
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
return|return
operator|(
name|Long
operator|)
name|o
return|;
if|if
condition|(
name|o
operator|instanceof
name|Number
condition|)
return|return
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|longValue
argument_list|()
return|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
DECL|method|delta
specifier|public
name|Integer
name|delta
parameter_list|(
name|Object
name|expected
parameter_list|,
name|Object
name|actual
parameter_list|)
block|{
try|try
block|{
name|Integer
name|expectedInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|expected
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|actualInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|actual
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|_delta
argument_list|(
name|expectedInt
argument_list|,
name|actualInt
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|_delta
specifier|protected
name|int
name|_delta
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
end_enum

end_unit

