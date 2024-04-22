import { UserProvider } from "@auth0/nextjs-auth0/client";
import { Providers } from "./providers";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <UserProvider>
        <body>
          <Providers>{children}</Providers>
        </body>
      </UserProvider>
    </html>
  );
}
