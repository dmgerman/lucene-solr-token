begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|LinkedHashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|rowset
operator|.
name|serial
operator|.
name|SerialClob
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
name|handler
operator|.
name|dataimport
operator|.
name|AbstractDataImportHandlerTestCase
operator|.
name|TestContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|AbstractDIHCacheTestCase
specifier|public
class|class
name|AbstractDIHCacheTestCase
block|{
DECL|field|Feb21_2011
specifier|protected
specifier|static
specifier|final
name|Date
name|Feb21_2011
init|=
operator|new
name|Date
argument_list|(
literal|1298268000000l
argument_list|)
decl_stmt|;
DECL|field|fieldTypes
specifier|protected
specifier|final
name|String
index|[]
name|fieldTypes
init|=
block|{
literal|"INTEGER"
block|,
literal|"BIGDECIMAL"
block|,
literal|"STRING"
block|,
literal|"STRING"
block|,
literal|"FLOAT"
block|,
literal|"DATE"
block|,
literal|"CLOB"
block|}
decl_stmt|;
DECL|field|fieldNames
specifier|protected
specifier|final
name|String
index|[]
name|fieldNames
init|=
block|{
literal|"a_id"
block|,
literal|"PI"
block|,
literal|"letter"
block|,
literal|"examples"
block|,
literal|"a_float"
block|,
literal|"a_date"
block|,
literal|"DESCRIPTION"
block|}
decl_stmt|;
DECL|field|data
specifier|protected
name|List
argument_list|<
name|ControlData
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|ControlData
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|APPLE
specifier|protected
name|Clob
name|APPLE
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
try|try
block|{
name|APPLE
operator|=
operator|new
name|SerialClob
argument_list|(
operator|new
name|String
argument_list|(
literal|"Apples grow on trees and they are good to eat."
argument_list|)
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqe
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not Set up Test"
argument_list|)
expr_stmt|;
block|}
comment|// The first row needs to have all non-null fields,
comment|// otherwise we would have to always send the fieldTypes& fieldNames as CacheProperties when building.
name|data
operator|=
operator|new
name|ArrayList
argument_list|<
name|ControlData
argument_list|>
argument_list|()
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"A"
block|,
literal|"Apple"
block|,
operator|new
name|Float
argument_list|(
literal|1.11
argument_list|)
block|,
name|Feb21_2011
block|,
name|APPLE
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"B"
block|,
literal|"Ball"
block|,
operator|new
name|Float
argument_list|(
literal|2.22
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|4
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"D"
block|,
literal|"Dog"
block|,
operator|new
name|Float
argument_list|(
literal|4.44
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"C"
block|,
literal|"Cookie"
block|,
operator|new
name|Float
argument_list|(
literal|3.33
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|4
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"D"
block|,
literal|"Daisy"
block|,
operator|new
name|Float
argument_list|(
literal|4.44
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|4
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"D"
block|,
literal|"Drawing"
block|,
operator|new
name|Float
argument_list|(
literal|4.44
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"E"
block|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Eggplant"
argument_list|,
literal|"Ear"
argument_list|,
literal|"Elephant"
argument_list|,
literal|"Engine"
argument_list|)
block|,
operator|new
name|Float
argument_list|(
literal|5.55
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|APPLE
operator|=
literal|null
expr_stmt|;
name|data
operator|=
literal|null
expr_stmt|;
block|}
comment|//A limitation of this test class is that the primary key needs to be the first one in the list.
comment|//DIHCaches, however, can handle any field being the primary key.
DECL|class|ControlData
class|class
name|ControlData
implements|implements
name|Comparable
argument_list|<
name|ControlData
argument_list|>
implements|,
name|Iterable
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|data
name|Object
index|[]
name|data
decl_stmt|;
DECL|method|ControlData
name|ControlData
parameter_list|(
name|Object
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|ControlData
name|cd
parameter_list|)
block|{
name|Comparable
name|c1
init|=
operator|(
name|Comparable
operator|)
name|data
index|[
literal|0
index|]
decl_stmt|;
name|Comparable
name|c2
init|=
operator|(
name|Comparable
operator|)
name|cd
operator|.
name|data
index|[
literal|0
index|]
decl_stmt|;
return|return
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
return|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|data
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
DECL|method|loadData
specifier|protected
name|void
name|loadData
parameter_list|(
name|DIHCache
name|cache
parameter_list|,
name|List
argument_list|<
name|ControlData
argument_list|>
name|theData
parameter_list|,
name|String
index|[]
name|theFieldNames
parameter_list|,
name|boolean
name|keepOrdered
parameter_list|)
block|{
for|for
control|(
name|ControlData
name|cd
range|:
name|theData
control|)
block|{
name|cache
operator|.
name|add
argument_list|(
name|controlDataToMap
argument_list|(
name|cd
argument_list|,
name|theFieldNames
argument_list|,
name|keepOrdered
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|extractDataInKeyOrder
specifier|protected
name|List
argument_list|<
name|ControlData
argument_list|>
name|extractDataInKeyOrder
parameter_list|(
name|DIHCache
name|cache
parameter_list|,
name|String
index|[]
name|theFieldNames
parameter_list|)
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|cacheIter
init|=
name|cache
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|cacheIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|data
operator|.
name|add
argument_list|(
name|mapToObjectArray
argument_list|(
name|cacheIter
operator|.
name|next
argument_list|()
argument_list|,
name|theFieldNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listToControlData
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|//This method assumes that the Primary Keys are integers and that the first id=1.
comment|//It will look for id's sequentially until one is skipped, then will stop.
DECL|method|extractDataByKeyLookup
specifier|protected
name|List
argument_list|<
name|ControlData
argument_list|>
name|extractDataByKeyLookup
parameter_list|(
name|DIHCache
name|cache
parameter_list|,
name|String
index|[]
name|theFieldNames
parameter_list|)
block|{
name|int
name|recId
init|=
literal|1
decl_stmt|;
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|listORecs
init|=
name|cache
operator|.
name|iterator
argument_list|(
name|recId
argument_list|)
decl_stmt|;
if|if
condition|(
name|listORecs
operator|==
literal|null
condition|)
block|{
break|break;
block|}
while|while
condition|(
name|listORecs
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|data
operator|.
name|add
argument_list|(
name|mapToObjectArray
argument_list|(
name|listORecs
operator|.
name|next
argument_list|()
argument_list|,
name|theFieldNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|recId
operator|++
expr_stmt|;
block|}
return|return
name|listToControlData
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|method|listToControlData
specifier|protected
name|List
argument_list|<
name|ControlData
argument_list|>
name|listToControlData
parameter_list|(
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|)
block|{
name|List
argument_list|<
name|ControlData
argument_list|>
name|returnData
init|=
operator|new
name|ArrayList
argument_list|<
name|ControlData
argument_list|>
argument_list|(
name|data
operator|.
name|size
argument_list|()
argument_list|)
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
name|data
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|returnData
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|returnData
return|;
block|}
DECL|method|mapToObjectArray
specifier|protected
name|Object
index|[]
name|mapToObjectArray
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rec
parameter_list|,
name|String
index|[]
name|theFieldNames
parameter_list|)
block|{
name|Object
index|[]
name|oos
init|=
operator|new
name|Object
index|[
name|theFieldNames
operator|.
name|length
index|]
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
name|theFieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|oos
index|[
name|i
index|]
operator|=
name|rec
operator|.
name|get
argument_list|(
name|theFieldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|oos
return|;
block|}
DECL|method|compareData
specifier|protected
name|void
name|compareData
parameter_list|(
name|List
argument_list|<
name|ControlData
argument_list|>
name|theControl
parameter_list|,
name|List
argument_list|<
name|ControlData
argument_list|>
name|test
parameter_list|)
block|{
comment|// The test data should come back primarily in Key order and secondarily in insertion order.
name|List
argument_list|<
name|ControlData
argument_list|>
name|control
init|=
operator|new
name|ArrayList
argument_list|<
name|ControlData
argument_list|>
argument_list|(
name|theControl
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|control
argument_list|)
expr_stmt|;
name|StringBuilder
name|errors
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|size
argument_list|()
operator|!=
name|control
operator|.
name|size
argument_list|()
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Returned data has "
operator|+
name|test
operator|.
name|size
argument_list|()
operator|+
literal|" records.  expected: "
operator|+
name|control
operator|.
name|size
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|control
operator|.
name|size
argument_list|()
operator|&&
name|i
operator|<
name|test
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
index|[]
name|controlRec
init|=
name|control
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
decl_stmt|;
name|Object
index|[]
name|testRec
init|=
name|test
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
decl_stmt|;
if|if
condition|(
name|testRec
operator|.
name|length
operator|!=
name|controlRec
operator|.
name|length
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Record indexAt="
operator|+
name|i
operator|+
literal|" has "
operator|+
name|testRec
operator|.
name|length
operator|+
literal|" data elements.  extpected: "
operator|+
name|controlRec
operator|.
name|length
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|controlRec
operator|.
name|length
operator|&&
name|j
operator|<
name|testRec
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Object
name|controlObj
init|=
name|controlRec
index|[
name|j
index|]
decl_stmt|;
name|Object
name|testObj
init|=
name|testRec
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|controlObj
operator|==
literal|null
operator|&&
name|testObj
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Record indexAt="
operator|+
name|i
operator|+
literal|", Data Element indexAt="
operator|+
name|j
operator|+
literal|" is not NULL as expected.\n"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|controlObj
operator|!=
literal|null
operator|&&
name|testObj
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Record indexAt="
operator|+
name|i
operator|+
literal|", Data Element indexAt="
operator|+
name|j
operator|+
literal|" is NULL.  Expected: "
operator|+
name|controlObj
operator|+
literal|" (class="
operator|+
name|controlObj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")\n"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|controlObj
operator|!=
literal|null
operator|&&
name|testObj
operator|!=
literal|null
operator|&&
name|controlObj
operator|instanceof
name|Clob
condition|)
block|{
name|String
name|controlString
init|=
name|clobToString
argument_list|(
operator|(
name|Clob
operator|)
name|controlObj
argument_list|)
decl_stmt|;
name|String
name|testString
init|=
name|clobToString
argument_list|(
operator|(
name|Clob
operator|)
name|testObj
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|controlString
operator|.
name|equals
argument_list|(
name|testString
argument_list|)
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Record indexAt="
operator|+
name|i
operator|+
literal|", Data Element indexAt="
operator|+
name|j
operator|+
literal|" has: "
operator|+
name|testString
operator|+
literal|" (class=Clob) ... Expected: "
operator|+
name|controlString
operator|+
literal|" (class=Clob)\n"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|controlObj
operator|!=
literal|null
operator|&&
operator|!
name|controlObj
operator|.
name|equals
argument_list|(
name|testObj
argument_list|)
condition|)
block|{
name|errors
operator|.
name|append
argument_list|(
literal|"-Record indexAt="
operator|+
name|i
operator|+
literal|", Data Element indexAt="
operator|+
name|j
operator|+
literal|" has: "
operator|+
name|testObj
operator|+
literal|" (class="
operator|+
name|testObj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|") ... Expected: "
operator|+
name|controlObj
operator|+
literal|" (class="
operator|+
name|controlObj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|errors
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|errors
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|controlDataToMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|controlDataToMap
parameter_list|(
name|ControlData
name|cd
parameter_list|,
name|String
index|[]
name|theFieldNames
parameter_list|,
name|boolean
name|keepOrdered
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|keepOrdered
condition|)
block|{
name|rec
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rec
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cd
operator|.
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fieldName
init|=
name|theFieldNames
index|[
name|i
index|]
decl_stmt|;
name|Object
name|data
init|=
name|cd
operator|.
name|data
index|[
name|i
index|]
decl_stmt|;
name|rec
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|rec
return|;
block|}
DECL|method|stringArrayToCommaDelimitedList
specifier|protected
name|String
name|stringArrayToCommaDelimitedList
parameter_list|(
name|String
index|[]
name|strs
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|a
range|:
name|strs
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|clobToString
specifier|protected
name|String
name|clobToString
parameter_list|(
name|Clob
name|cl
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|Reader
name|in
init|=
name|cl
operator|.
name|getCharacterStream
argument_list|()
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|numGot
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|numGot
operator|=
name|in
operator|.
name|read
argument_list|(
name|cbuf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|numGot
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getContext
specifier|public
specifier|static
name|Context
name|getContext
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
parameter_list|)
block|{
name|VariableResolverImpl
name|resolver
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
specifier|final
name|Context
name|delegate
init|=
operator|new
name|ContextImpl
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestContext
argument_list|(
name|entityAttrs
argument_list|,
name|delegate
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

