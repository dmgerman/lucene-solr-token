begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|document
operator|.
name|InetAddressRange
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * Random testing for {@link InetAddressRange}  */
end_comment

begin_class
DECL|class|TestInetAddressRangeQueries
specifier|public
class|class
name|TestInetAddressRangeQueries
extends|extends
name|BaseRangeFieldQueryTestCase
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"ipRangeField"
decl_stmt|;
DECL|field|ipVersion
specifier|private
name|IPVersion
name|ipVersion
decl_stmt|;
DECL|enum|IPVersion
DECL|enum constant|IPv4
DECL|enum constant|IPv6
specifier|private
enum|enum
name|IPVersion
block|{
name|IPv4
block|,
name|IPv6
block|}
annotation|@
name|Override
DECL|method|nextRange
specifier|protected
name|Range
name|nextRange
parameter_list|(
name|int
name|dimensions
parameter_list|)
throws|throws
name|Exception
block|{
name|InetAddress
name|min
init|=
name|nextInetaddress
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bMin
init|=
name|min
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|InetAddress
name|max
init|=
name|nextInetaddress
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bMax
init|=
name|max
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMin
operator|.
name|length
argument_list|,
name|bMin
argument_list|,
literal|0
argument_list|,
name|bMax
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|IpRange
argument_list|(
name|max
argument_list|,
name|min
argument_list|)
return|;
block|}
return|return
operator|new
name|IpRange
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/** return random IPv4 or IPv6 address */
DECL|method|nextInetaddress
specifier|private
name|InetAddress
name|nextInetaddress
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|byte
index|[]
name|b
decl_stmt|;
switch|switch
condition|(
name|ipVersion
condition|)
block|{
case|case
name|IPv4
case|:
name|b
operator|=
operator|new
name|byte
index|[
literal|4
index|]
expr_stmt|;
break|break;
case|case
name|IPv6
case|:
name|b
operator|=
operator|new
name|byte
index|[
literal|16
index|]
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"incorrect IP version: "
operator|+
name|ipVersion
argument_list|)
throw|;
block|}
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|b
argument_list|)
return|;
block|}
comment|/** randomly select version across tests */
DECL|method|ipVersion
specifier|private
name|IPVersion
name|ipVersion
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|IPVersion
operator|.
name|IPv4
else|:
name|IPVersion
operator|.
name|IPv6
return|;
block|}
annotation|@
name|Override
DECL|method|testRandomTiny
specifier|public
name|void
name|testRandomTiny
parameter_list|()
throws|throws
name|Exception
block|{
name|ipVersion
operator|=
name|ipVersion
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRandomTiny
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
name|ipVersion
operator|=
name|ipVersion
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRandomMedium
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRandomMedium
specifier|public
name|void
name|testRandomMedium
parameter_list|()
throws|throws
name|Exception
block|{
name|ipVersion
operator|=
name|ipVersion
argument_list|()
expr_stmt|;
name|super
operator|.
name|testMultiValued
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nightly
annotation|@
name|Override
DECL|method|testRandomBig
specifier|public
name|void
name|testRandomBig
parameter_list|()
throws|throws
name|Exception
block|{
name|ipVersion
operator|=
name|ipVersion
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRandomBig
argument_list|()
expr_stmt|;
block|}
comment|/** return random range */
annotation|@
name|Override
DECL|method|newRangeField
specifier|protected
name|InetAddressRange
name|newRangeField
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
operator|new
name|InetAddressRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** return random intersects query */
annotation|@
name|Override
DECL|method|newIntersectsQuery
specifier|protected
name|Query
name|newIntersectsQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|InetAddressRange
operator|.
name|newIntersectsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** return random contains query */
annotation|@
name|Override
DECL|method|newContainsQuery
specifier|protected
name|Query
name|newContainsQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|InetAddressRange
operator|.
name|newContainsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** return random within query */
annotation|@
name|Override
DECL|method|newWithinQuery
specifier|protected
name|Query
name|newWithinQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|InetAddressRange
operator|.
name|newWithinQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** return random crosses query */
annotation|@
name|Override
DECL|method|newCrossesQuery
specifier|protected
name|Query
name|newCrossesQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|InetAddressRange
operator|.
name|newCrossesQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|IpRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** encapsulated IpRange for test validation */
DECL|class|IpRange
specifier|private
class|class
name|IpRange
extends|extends
name|Range
block|{
DECL|field|min
name|InetAddress
name|min
decl_stmt|;
DECL|field|max
name|InetAddress
name|max
decl_stmt|;
DECL|method|IpRange
name|IpRange
parameter_list|(
name|InetAddress
name|min
parameter_list|,
name|InetAddress
name|max
parameter_list|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDimensions
specifier|protected
name|int
name|numDimensions
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|protected
name|InetAddress
name|getMin
parameter_list|(
name|int
name|dim
parameter_list|)
block|{
return|return
name|min
return|;
block|}
annotation|@
name|Override
DECL|method|setMin
specifier|protected
name|void
name|setMin
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|byte
index|[]
name|v
init|=
operator|(
operator|(
name|InetAddress
operator|)
name|val
operator|)
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|v
operator|.
name|length
argument_list|,
name|min
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|,
name|v
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
name|max
operator|=
operator|(
name|InetAddress
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
operator|(
name|InetAddress
operator|)
name|val
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|protected
name|InetAddress
name|getMax
parameter_list|(
name|int
name|dim
parameter_list|)
block|{
return|return
name|max
return|;
block|}
annotation|@
name|Override
DECL|method|setMax
specifier|protected
name|void
name|setMax
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|byte
index|[]
name|v
init|=
operator|(
operator|(
name|InetAddress
operator|)
name|val
operator|)
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|v
operator|.
name|length
argument_list|,
name|max
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|,
name|v
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
name|min
operator|=
operator|(
name|InetAddress
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
operator|(
name|InetAddress
operator|)
name|val
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isEqual
specifier|protected
name|boolean
name|isEqual
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|IpRange
name|other
init|=
operator|(
name|IpRange
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|min
operator|.
name|equals
argument_list|(
name|other
operator|.
name|min
argument_list|)
operator|&&
name|this
operator|.
name|max
operator|.
name|equals
argument_list|(
name|other
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isDisjoint
specifier|protected
name|boolean
name|isDisjoint
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|IpRange
name|other
init|=
operator|(
name|IpRange
operator|)
name|o
decl_stmt|;
name|byte
index|[]
name|bMin
init|=
name|min
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bMax
init|=
name|max
operator|.
name|getAddress
argument_list|()
decl_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMin
operator|.
name|length
argument_list|,
name|bMin
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|max
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMax
operator|.
name|length
argument_list|,
name|bMax
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|min
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|protected
name|boolean
name|isWithin
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|IpRange
name|other
init|=
operator|(
name|IpRange
operator|)
name|o
decl_stmt|;
name|byte
index|[]
name|bMin
init|=
name|min
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bMax
init|=
name|max
operator|.
name|getAddress
argument_list|()
decl_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMin
operator|.
name|length
argument_list|,
name|bMin
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|min
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|>=
literal|0
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMax
operator|.
name|length
argument_list|,
name|bMax
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|max
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|<=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|protected
name|boolean
name|contains
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|IpRange
name|other
init|=
operator|(
name|IpRange
operator|)
name|o
decl_stmt|;
name|byte
index|[]
name|bMin
init|=
name|min
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bMax
init|=
name|max
operator|.
name|getAddress
argument_list|()
decl_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMin
operator|.
name|length
argument_list|,
name|bMin
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|min
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|<=
literal|0
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|bMax
operator|.
name|length
argument_list|,
name|bMax
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|max
operator|.
name|getAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|>=
literal|0
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
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"Box("
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|min
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|max
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
