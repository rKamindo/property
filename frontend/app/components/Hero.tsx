"use client";

import {
  Button,
  Flex,
  Heading,
  Image,
  Stack,
  Text,
  useBreakpointValue,
} from "@chakra-ui/react";
import NextImage from "next/image";

export default function SplitScreen() {
  return (
    <Stack minH={"100vh"} direction={{ base: "column", md: "row" }}>
      <Flex p={8} flex={1} align={"center"} justify={"center"}>
        <Stack spacing={6} w={"full"} maxW={"lg"}>
          <NextImage
            width={350}
            height={350}
            src="/logo.svg"
            className="hidden md:block"
            alt="Screenshots of the dashboard project showing desktop version"
          />
          <Heading fontSize={{ base: "3xl", md: "4xl", lg: "5xl" }}>
            <Text
              as={"span"}
              position={"relative"}
              _after={{
                content: "''",
                width: "full",
                height: useBreakpointValue({ base: "20%", md: "30%" }),
                position: "absolute",
                bottom: 1,
                left: 0,
                bg: "blue.400",
                zIndex: -1,
              }}
            >
              Simplify
            </Text>
            <br />{" "}
            <Text color={"blue.400"} as={"span"}>
              property management
            </Text>{" "}
          </Heading>
          <Text fontSize={{ base: "md", lg: "lg" }} color={"gray.500"}>
            Streamline your property management workflow. Manage units, tenants,
            leases, and stay on top of maintenance – all from one intuitive
            platform.
          </Text>
          <Stack direction={{ base: "column", md: "row" }} spacing={4}>
            <Button
              as={"a"}
              rounded={"full"}
              bg={"blue.400"}
              color={"white"}
              href={"/api/auth/login"}
              _hover={{
                bg: "blue.500",
              }}
            >
              Get started
            </Button>
          </Stack>
        </Stack>
      </Flex>
      <Flex flex={1}>
        <Image alt={"Login Image"} objectFit={"cover"} src={"heroimage.jpg"} />
      </Flex>
    </Stack>
  );
}
