package com.lzx.cloud.leetCode;

import java.util.Arrays;

public class leetCode88 {

    public static void main(String[] args) {
        int[] nums1 = new int[]{1, 2, 3, 0, 0, 0};
        int m = 3;
        int[] nums2 = new int[]{2, 5, 6};
        int n = 3;

        for (int i = 0; i < n; i++) {
            nums1[m + i] = nums2[i];
        }
        Arrays.sort(nums1);

        Arrays.stream(nums1).forEach(System.out::println);
    }
}
