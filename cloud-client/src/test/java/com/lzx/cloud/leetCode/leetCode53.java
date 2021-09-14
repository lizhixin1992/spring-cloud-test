package com.lzx.cloud.leetCode;

public class leetCode53 {

    public static void main(String[] args) {
//        动态规划
        int[] nums = new int[]{-2,1,-3,4,-1,2,1,-5,4};
        int pre = 0, maxAns = nums[0];
        for (int x : nums) {
            pre = Math.max(pre + x, x);
            maxAns = Math.max(maxAns, pre);
        }
        System.out.println(maxAns);
    }
}
