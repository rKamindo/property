"use client";
import { useUser } from "@auth0/nextjs-auth0/client";
import Hero from "./components/Hero";
import SidebarWithHeader from "./components/SideBarWithHeader";
import { Center, Spinner } from "@chakra-ui/react";

export default function Index() {
  const { user, isLoading } = useUser();
  if (isLoading) {
    return (
      <Center h="100vh" w="100vw">
        <Spinner size="xl" />
      </Center>
    );
  }
  if (user) {
    return <SidebarWithHeader />;
  } else {
    return <Hero />;
  }
}
