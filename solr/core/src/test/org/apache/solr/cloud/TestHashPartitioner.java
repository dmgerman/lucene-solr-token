begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|CompositeIdRouter
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|DocRouter
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
name|cloud
operator|.
name|DocRouter
operator|.
name|Range
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
name|cloud
operator|.
name|PlainIdRouter
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
name|cloud
operator|.
name|Slice
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
name|Hash
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
name|StrUtils
import|;
end_import

begin_class
DECL|class|TestHashPartitioner
specifier|public
class|class
name|TestHashPartitioner
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testMapHashes
specifier|public
name|void
name|testMapHashes
parameter_list|()
throws|throws
name|Exception
block|{
name|DocRouter
name|hp
init|=
name|DocRouter
operator|.
name|DEFAULT
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
decl_stmt|;
comment|// make sure the partitioner uses the "natural" boundaries and doesn't suffer from an off-by-one
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
name|hp
operator|.
name|fullRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x80000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xffffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x00000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x7fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|0x7fffffff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x00000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x3fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x40000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x7fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|30000
condition|;
name|i
operator|+=
literal|13
control|)
block|{
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
name|i
argument_list|,
name|hp
operator|.
name|fullRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First range does not start before "
operator|+
name|Integer
operator|.
name|MIN_VALUE
operator|+
literal|" it is:"
operator|+
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
operator|<=
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Last range does not end after "
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|" it is:"
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|max
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|max
operator|>=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|String
name|s
init|=
name|range
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Range
name|newRange
init|=
name|hp
operator|.
name|fromString
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|range
argument_list|,
name|newRange
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|hash
specifier|public
name|int
name|hash
parameter_list|(
name|String
name|id
parameter_list|)
block|{
comment|// our hashing is defined to be murmurhash3 on the UTF-8 bytes of the key.
return|return
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|testHashCodes
specifier|public
name|void
name|testHashCodes
parameter_list|()
throws|throws
name|Exception
block|{
name|DocRouter
name|router
init|=
name|DocRouter
operator|.
name|getDocRouter
argument_list|(
name|PlainIdRouter
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|router
operator|instanceof
name|PlainIdRouter
argument_list|)
expr_stmt|;
name|DocCollection
name|coll
init|=
name|createCollection
argument_list|(
literal|4
argument_list|,
name|router
argument_list|)
decl_stmt|;
name|doNormalIdHashing
argument_list|(
name|coll
argument_list|)
expr_stmt|;
block|}
DECL|method|doNormalIdHashing
specifier|public
name|void
name|doNormalIdHashing
parameter_list|(
name|DocCollection
name|coll
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|coll
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"b"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"c"
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"d"
argument_list|,
literal|"shard3"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"e"
argument_list|,
literal|"shard4"
argument_list|)
expr_stmt|;
block|}
DECL|method|doId
specifier|public
name|void
name|doId
parameter_list|(
name|DocCollection
name|coll
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|expectedShard
parameter_list|)
block|{
name|doIndex
argument_list|(
name|coll
argument_list|,
name|id
argument_list|,
name|expectedShard
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
name|coll
argument_list|,
name|id
argument_list|,
name|expectedShard
argument_list|)
expr_stmt|;
block|}
DECL|method|doIndex
specifier|public
name|void
name|doIndex
parameter_list|(
name|DocCollection
name|coll
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|expectedShard
parameter_list|)
block|{
name|DocRouter
name|router
init|=
name|coll
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Slice
name|target
init|=
name|router
operator|.
name|getTargetSlice
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|coll
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedShard
argument_list|,
name|target
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doQuery
specifier|public
name|void
name|doQuery
parameter_list|(
name|DocCollection
name|coll
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|expectedShards
parameter_list|)
block|{
name|DocRouter
name|router
init|=
name|coll
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|router
operator|.
name|getSearchSlices
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|coll
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedShardStr
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|expectedShards
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|expectedSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|expectedShardStr
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|obtainedSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|obtainedSet
operator|.
name|add
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|slices
operator|.
name|size
argument_list|()
argument_list|,
name|obtainedSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure no repeated slices
name|assertEquals
argument_list|(
name|expectedSet
argument_list|,
name|obtainedSet
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompositeHashCodes
specifier|public
name|void
name|testCompositeHashCodes
parameter_list|()
throws|throws
name|Exception
block|{
name|DocRouter
name|router
init|=
name|DocRouter
operator|.
name|getDocRouter
argument_list|(
name|CompositeIdRouter
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|router
operator|instanceof
name|CompositeIdRouter
argument_list|)
expr_stmt|;
name|router
operator|=
name|DocRouter
operator|.
name|DEFAULT
expr_stmt|;
name|assertTrue
argument_list|(
name|router
operator|instanceof
name|CompositeIdRouter
argument_list|)
expr_stmt|;
name|DocCollection
name|coll
init|=
name|createCollection
argument_list|(
literal|4
argument_list|,
name|router
argument_list|)
decl_stmt|;
name|doNormalIdHashing
argument_list|(
name|coll
argument_list|)
expr_stmt|;
comment|// ensure that the shard hashed to is only dependent on the first part of the compound key
name|doId
argument_list|(
name|coll
argument_list|,
literal|"b!foo"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"c!bar"
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"d!baz"
argument_list|,
literal|"shard3"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"e!qux"
argument_list|,
literal|"shard4"
argument_list|)
expr_stmt|;
comment|// syntax to specify bits.
comment|// Anything over 2 bits should give the same results as above (since only top 2 bits
comment|// affect our 4 slice collection).
name|doId
argument_list|(
name|coll
argument_list|,
literal|"b/2!foo"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"c/2!bar"
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"d/2!baz"
argument_list|,
literal|"shard3"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"e/2!qux"
argument_list|,
literal|"shard4"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"b/32!foo"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"c/32!bar"
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"d/32!baz"
argument_list|,
literal|"shard3"
argument_list|)
expr_stmt|;
name|doId
argument_list|(
name|coll
argument_list|,
literal|"e/32!qux"
argument_list|,
literal|"shard4"
argument_list|)
expr_stmt|;
comment|// no bits allocated to the first part (kind of odd why anyone would do that though)
name|doIndex
argument_list|(
name|coll
argument_list|,
literal|"foo/0!b"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|doIndex
argument_list|(
name|coll
argument_list|,
literal|"foo/0!c"
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
name|doIndex
argument_list|(
name|coll
argument_list|,
literal|"foo/0!d"
argument_list|,
literal|"shard3"
argument_list|)
expr_stmt|;
name|doIndex
argument_list|(
name|coll
argument_list|,
literal|"foo/0!e"
argument_list|,
literal|"shard4"
argument_list|)
expr_stmt|;
comment|// means cover whole range on the query side
name|doQuery
argument_list|(
name|coll
argument_list|,
literal|"foo/0!"
argument_list|,
literal|"shard1,shard2,shard3,shard4"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
name|coll
argument_list|,
literal|"b/1!"
argument_list|,
literal|"shard1,shard2"
argument_list|)
expr_stmt|;
comment|// top bit of hash(b)==1, so shard1 and shard2
name|doQuery
argument_list|(
name|coll
argument_list|,
literal|"d/1!"
argument_list|,
literal|"shard3,shard4"
argument_list|)
expr_stmt|;
comment|// top bit of hash(b)==0, so shard3 and shard4
block|}
comment|/***   public void testPrintHashCodes() throws Exception {    // from negative to positive, the upper bits of the hash ranges should be    // shard1: 11    // shard2: 10    // shard3: 00    // shard4: 01     String[] highBitsToShard = {"shard3","shard4","shard1","shard2"};      for (int i = 0; i<26; i++) {       String id  = new String(Character.toChars('a'+i));       int hash = hash(id);       System.out.println("hash of " + id + " is " + Integer.toHexString(hash) + " high bits=" + (hash>>>30)           + " shard="+highBitsToShard[hash>>>30]);     }   }   ***/
DECL|method|createCollection
name|DocCollection
name|createCollection
parameter_list|(
name|int
name|nSlices
parameter_list|,
name|DocRouter
name|router
parameter_list|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
name|router
operator|.
name|partitionRange
argument_list|(
name|nSlices
argument_list|,
name|router
operator|.
name|fullRange
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
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
name|ranges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Range
name|range
init|=
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
operator|new
name|Slice
argument_list|(
literal|"shard"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
literal|null
argument_list|,
name|map
argument_list|(
literal|"range"
argument_list|,
name|range
argument_list|)
argument_list|)
decl_stmt|;
name|slices
operator|.
name|put
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
name|DocCollection
name|coll
init|=
operator|new
name|DocCollection
argument_list|(
literal|"collection1"
argument_list|,
name|slices
argument_list|,
literal|null
argument_list|,
name|router
argument_list|)
decl_stmt|;
return|return
name|coll
return|;
block|}
comment|// from negative to positive, the upper bits of the hash ranges should be
comment|// shard1: top bits:10  80000000:bfffffff
comment|// shard2: top bits:11  c0000000:ffffffff
comment|// shard3: top bits:00  00000000:3fffffff
comment|// shard4: top bits:01  40000000:7fffffff
comment|/***    hash of a is 3c2569b2 high bits=0 shard=shard3    hash of b is 95de7e03 high bits=2 shard=shard1    hash of c is e132d65f high bits=3 shard=shard2    hash of d is 27191473 high bits=0 shard=shard3    hash of e is 656c4367 high bits=1 shard=shard4    hash of f is 2b64883b high bits=0 shard=shard3    hash of g is f18ae416 high bits=3 shard=shard2    hash of h is d482b2d3 high bits=3 shard=shard2    hash of i is 811a702b high bits=2 shard=shard1    hash of j is ca745a39 high bits=3 shard=shard2    hash of k is cfbda5d1 high bits=3 shard=shard2    hash of l is 1d5d6a2c high bits=0 shard=shard3    hash of m is 5ae4385c high bits=1 shard=shard4    hash of n is c651d8ac high bits=3 shard=shard2    hash of o is 68348473 high bits=1 shard=shard4    hash of p is 986fdf9a high bits=2 shard=shard1    hash of q is ff8209e8 high bits=3 shard=shard2    hash of r is 5c9373f1 high bits=1 shard=shard4    hash of s is ff4acaf1 high bits=3 shard=shard2    hash of t is ca87df4d high bits=3 shard=shard2    hash of u is 62203ae0 high bits=1 shard=shard4    hash of v is bdafcc55 high bits=2 shard=shard1    hash of w is ff439d1f high bits=3 shard=shard2    hash of x is 3e9a9b1b high bits=0 shard=shard3    hash of y is 477d9216 high bits=1 shard=shard4    hash of z is c1f69a17 high bits=3 shard=shard2    ***/
block|}
end_class

end_unit

