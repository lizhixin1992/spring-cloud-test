package com.lzx.cloud.leetCode;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class leetCode217 {

    public static void main(String[] args) {
        int[] nums = new int[]{1, 1, 1, 3, 3, 4, 3, 2, 4, 2};
//        Map<String, Integer> map = new HashMap<String, Integer>();
//
//        for (int i = 0; i < nums.length; i++) {
//            int num = nums[i];
//            if (map.get(String.valueOf(num)) != null) {
//                System.out.println(true);
//                break;
//            }else {
//                map.put(String.valueOf(num), 1);
//            }
//        }


//        Arrays.sort(nums);
//        for (int i = 0; i < (nums.length - 1); i++) {
//            if(nums[i] == nums[i+1]){
//                System.out.println(true);
//                break;
//            }
//        }

//        Set<Integer> set = new HashSet<>();
//        for (int num : nums) {
//            if(!set.add(num)){
//                System.out.println(true);
//                break;
//            }
//        }

        System.out.println(IntStream.of(nums).distinct().count() != nums.length);
    }
}
