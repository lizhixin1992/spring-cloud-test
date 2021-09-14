package com.lzx.cloud.leetCode;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class leetCode1 {
    public static void main(String[] args) {
        int[] a = new int[2];
        int[] nums = new int[]{2, 7, 11, 15};
        int target = 9;

//        for (int i = 0; i < nums.length; i++) {
//            int num = nums[i];
//            for (int j = i + 1; j < nums.length; j++) {
//                int num1 = nums[j];
//                if (num + num1 == target) {
//                   a[0] = i;
//                   a[1] = j;
//                   break;
//                }
//            }
//        }
//        System.out.println(a[0] + "+" + a[1]);


        Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; ++i) {
            if (hashtable.containsKey(target - nums[i])) {
                System.out.println(JSON.toJSONString(Collections.singletonList(new int[]{hashtable.get(target - nums[i]), i})));
            }
            hashtable.put(nums[i], i);
        }
        System.out.println(JSON.toJSONString(Collections.singletonList(new int[0])));

    }
}
